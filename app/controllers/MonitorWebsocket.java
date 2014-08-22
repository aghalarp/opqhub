package controllers;

import json.JsonUtils;
import json.MapRequest;
import json.PublicEventRequest;
import json.PublicEventResponse;
import json.PublicMapResponse;
import models.Event;
import models.OpqDevice;
import org.openpowerquality.protocol.OpqPacket;
import play.Logger;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.WebSocket;
import utils.DateUtils;
import utils.DbUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MonitorWebsocket extends Controller {
  /**
   * Create a WebSocket object who can receive connections, receive packets, and break connections.
   *
   * @return A WebSocket object.
   */
  public static WebSocket<String> handleSocket() {
    return new WebSocket<String>() {
      @Override
      public void onReady(In<String> in, final Out<String> out) {
        Logger.info("Front end websocket ready...");

        in.onMessage(new F.Callback<String>() {
          @Override
          public void invoke(String s) throws Throwable {

            switch (JsonUtils.getPacketType(s)) {
              case "public-update":
                Logger.info("Public update");
                MapRequest req = MapRequest.fromJson(s);
                if (req != null) {
                  handlePublicMap(out, req);
                }
                break;
              case "public-event-details":
                Logger.info("Event details");
                PublicEventRequest eventReq = PublicEventRequest.fromJson(s);
                if (eventReq != null) {
                  handleEventDetails(out, eventReq);
                }
                break;
              default:
                Logger.info("Does not match any");
                break;
            }
          }
        });

        in.onClose(new F.Callback0() {
          @Override
          public void invoke() throws Throwable {

            Logger.info("Websocket disconnected");
          }
        });
      }
    };
  }

  private static void handlePublicMap(final WebSocket.Out<String> out, MapRequest req) {
    PublicMapResponse resp = new PublicMapResponse();
    final int MAX_EVENTS = 100;
    // Did we get starting and ending timestamps in the request?
    // If not set to beginning of time and now.
    Long startTimestamp = (req.startTimestamp == null) ? 0 : req.startTimestamp;
    Long stopTimestamp = (req.stopTimestamp == null) ? DateUtils.getMillis() : req.stopTimestamp;

    // Since we're finding all devices that start with a particular grid id, we might need to find it's
    // parent gridId (the one currently visible) so we can place items on the map.
    int gridIdLength = ((String) req.visibleIds.toArray()[0]).length();

    Set<OpqDevice> affectedDevices = new HashSet<>();
    List<Event> events = DbUtils.getAnyLike(Event.class, "location.gridId", req.visibleIds).findList();
    Collections.sort(events);
    String gridId;

    // Update event statistics
    Map<String, String> tmpEvent;

    for (Event event : events) {
      if (event.getAccessKey().getOpqDevice().getSharingData() && event.getTimestamp() > startTimestamp &&
          event.getTimestamp() < stopTimestamp) {


        if (event.getEventType().equals(OpqPacket.PacketType.EVENT_FREQUENCY) && req.requestFrequency) {
          resp.totalFrequencyEvents++;
          resp.totalEvents++;
          gridId = event.getAccessKey().getOpqDevice().getLocation().getGridId().substring(0, gridIdLength);
          if (!resp.gridIdsToEvents.containsKey(gridId)) {
            resp.gridIdsToEvents.put(gridId, 0);
          }
          resp.gridIdsToEvents.put(gridId, resp.gridIdsToEvents.get(gridId) + 1);
          affectedDevices.add(event.getAccessKey().getOpqDevice());
          if (resp.events.size() < MAX_EVENTS) {
            tmpEvent = new HashMap<>();
            tmpEvent.put("id", event.getPrimaryKey().toString());
            tmpEvent.put("timestamp", DateUtils.toDateTime(event.getTimestamp()));
            tmpEvent.put("type", event.getEventType().getName().split(" ")[0]);
            tmpEvent.put("itic", "N/A");
            resp.events.add(tmpEvent);
          }
        }
        if (event.getEventType().equals(OpqPacket.PacketType.EVENT_VOLTAGE) && req.requestVoltage) {
          resp.totalVoltageEvents++;
          resp.totalEvents++;
          gridId = event.getAccessKey().getOpqDevice().getLocation().getGridId().substring(0, gridIdLength);
          if (!resp.gridIdsToEvents.containsKey(gridId)) {
            resp.gridIdsToEvents.put(gridId, 0);
          }
          resp.gridIdsToEvents.put(gridId, resp.gridIdsToEvents.get(gridId) + 1);
          affectedDevices.add(event.getAccessKey().getOpqDevice());
          if (resp.events.size() < MAX_EVENTS) {
            tmpEvent = new HashMap<>();
            tmpEvent.put("pk", event.getPrimaryKey().toString());
            tmpEvent.put("timestamp", DateUtils.toDateTime(event.getTimestamp()));
            tmpEvent.put("type", event.getEventType().getName().split(" ")[0]);
            tmpEvent.put("itic", "N/A");
            resp.events.add(tmpEvent);
          }
        }
      }
    }

    resp.totalAffectedDevices = affectedDevices.size();

    List<OpqDevice> devices = DbUtils.getAnyLike(OpqDevice.class, "location.gridId", req.visibleIds).findList();

    // Update device statistics
    for (OpqDevice device : devices) {
      if (device.getSharingData()) {
        gridId = device.getLocation().getGridId().substring(0, gridIdLength);
        if (!resp.gridIdsToDevices.containsKey(gridId)) {
          resp.gridIdsToDevices.put(gridId, 0);
        }
        resp.gridIdsToDevices.put(gridId, resp.gridIdsToDevices.get(gridId) + 1);

        resp.totalRegisteredDevices++;

        if (device.getLastHeartbeat() > DateUtils.getPastTime(DateUtils.getMillis(), DateUtils.TimeUnit.Day, 1)) {
          resp.totalActiveDevices++;
        }
      }
    }

    // Send response to client
    out.write(resp.toJson());
  }

  private static void handleEventDetails(final WebSocket.Out<String> out, PublicEventRequest req) {
    Event event = Event.find().byId(req.pk);
    PublicEventResponse resp = new PublicEventResponse();
    resp.timestamp = DateUtils.toDateTime(event.getTimestamp());
    resp.eventType = event.getEventType().getName().split(" ")[0];
    resp.frequency = event.getFrequency();
    resp.voltage = event.getVoltage();
    resp.duration = event.getDuration();
    resp.eventLevel = "Local";
    resp.gridId = event.getLocation().getGridId();
    resp.centerLat = (event.getLocation().getNorthEastLatitude() + event.getLocation().getSouthWestLatitude()) / 2;
    resp.centerLng = (event.getLocation().getNorthEastLongitude() + event.getLocation().getSouthWestLongitude()) / 2;
    resp.gridScale = event.getLocation().getGridScale();
    String waveformStr = event.getEventData().getWaveform();
    // Remove trailing comma
    waveformStr = waveformStr.substring(0, waveformStr.length() - 1);
    for(String doubleStr : waveformStr.split(",")) {
      try {
        resp.waveform.add(Double.parseDouble(doubleStr));
      } catch (NumberFormatException e) {
         Logger.warn(String.format("Invalid waveform data: %s\n%s", doubleStr, e.getMessage()));
      }
    }
    out.write(resp.toJson());
  }
}
