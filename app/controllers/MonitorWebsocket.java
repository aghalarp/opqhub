package controllers;

import json.JsonUtils;
import json.MapRequest;
import json.PrivateEventRequest;
import json.PrivateMapRequest;
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
import utils.PqUtils;
import utils.PqUtils.IticRegion;

import java.text.DecimalFormat;
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
    Logger.info("Attempting to create WS connection...");
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
              case "private-update":
                Logger.info("Private update");
                PrivateMapRequest reqp = PrivateMapRequest.fromJson(s);
                if (reqp != null) {
                  handlePrivateMap(out, reqp);
                }
                break;
              case "public-event-details":
                Logger.info("Event details");
                PublicEventRequest eventReq = PublicEventRequest.fromJson(s);
                if (eventReq != null) {
                  handleEventDetails(out, eventReq);
                }
                break;
              case "private-event-details":
                Logger.info("Private event details");
                PrivateEventRequest eventReqp = PrivateEventRequest.fromJson(s);
                if (eventReqp != null) {
                  handlePrivateEventDetails(out, eventReqp);
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
    DecimalFormat decimalFormat = new DecimalFormat("#.00");

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
    Integer[] tmpEventMetrics;
    IticRegion region;

    // For filter controls
    long duration;
    long maxDuration = Long.MIN_VALUE;
    double frequency;
    double minFrequency = Double.MAX_VALUE;
    double maxFrequency = Double.MIN_VALUE;
    double voltage;
    double minVoltage = Double.MAX_VALUE;
    double maxVoltage = Double.MIN_VALUE;
    long timestamp;
    long minTimestamp = DateUtils.getPastTime(DateUtils.getMillis(), DateUtils.TimeUnit.Year, 2);
    long maxTimestamp = DateUtils.getMillis();

    // Update event statistics
    Map<String, String> tmpEvent;
    for(Event event : events) {

      // For filter controls
      duration = event.getDuration();
      frequency = event.getFrequency();
      voltage = event.getVoltage();
      timestamp = event.getTimestamp();
      if(duration < 500 && duration > maxDuration) maxDuration = duration;
      if(frequency > 0 && frequency < minFrequency) minFrequency = frequency;
      if(frequency < 200 && frequency > maxFrequency) maxFrequency = frequency;
      if(voltage > 0 && voltage < minVoltage) minVoltage = voltage;
      if(voltage > maxVoltage) maxVoltage = voltage;
      if(timestamp > DateUtils.getPastTime(DateUtils.getMillis(), DateUtils.TimeUnit.Year, 2) && timestamp < minTimestamp) minTimestamp = timestamp;
      if(timestamp < DateUtils.getMillis() && timestamp > maxTimestamp) maxTimestamp = timestamp;
      resp.maxDuration = maxDuration;
      resp.minFrequency = minFrequency;
      resp.maxFrequency = maxFrequency;
      resp.minVoltage = minVoltage;
      resp.maxVoltage = maxVoltage;
      resp.minTimestamp = minTimestamp;
      resp.maxTimestamp = maxTimestamp;

      if(req.containsEvent(event)) {
        // Global metrics
        resp.totalFrequencyEvents = event.getEventType().equals(OpqPacket.PacketType.EVENT_FREQUENCY) ? resp.totalFrequencyEvents + 1 : resp.totalFrequencyEvents;
        resp.totalVoltageEvents = event.getEventType().equals(OpqPacket.PacketType.EVENT_VOLTAGE) ? resp.totalVoltageEvents + 1 : resp.totalVoltageEvents;
        resp.totalEvents++;

        // Grid id to metrics
        gridId = event.getAccessKey().getOpqDevice().getLocation().getGridId().substring(0, gridIdLength);
        if (!resp.gridIdToEventMetrics.containsKey(gridId)) {
          Integer[] z = {0, 0, 0};
          resp.gridIdToEventMetrics.put(gridId, z);
        }
        tmpEventMetrics = resp.gridIdToEventMetrics.get(gridId);
        region = PqUtils.getIticRegion(event.getDuration() * 1000, event.getVoltage());
        tmpEventMetrics[region.severity]++;
        resp.gridIdToEventMetrics.put(gridId, tmpEventMetrics);

        affectedDevices.add(event.getAccessKey().getOpqDevice());
        if (resp.events.size() < MAX_EVENTS) {
          tmpEvent = new HashMap<>();
          tmpEvent.put("id", event.getPrimaryKey().toString());
          tmpEvent.put("timestamp", DateUtils.toDateTime(event.getTimestamp()));
          tmpEvent.put("type", event.getEventType().getName().split(" ")[0]);
          tmpEvent.put("frequency", decimalFormat.format(event.getFrequency()));
          tmpEvent.put("voltage", decimalFormat.format(event.getVoltage()));
          tmpEvent.put("duration", event.getDuration().toString());
          resp.events.add(tmpEvent);
        }  
      }
    }
    resp.totalAffectedDevices = affectedDevices.size();

    List<OpqDevice> devices = DbUtils.getAnyLike(OpqDevice.class, "location.gridId", req.visibleIds).findList();
    // Update device statistics
    int i = 0;
    for (OpqDevice device : devices) {
      if (device.getSharingData()) {
        gridId = device.getLocation().getGridId().substring(0, gridIdLength);
        if (!resp.gridIdsToDevices.containsKey(gridId)) {
          resp.gridIdsToDevices.put(gridId, 0);
        }
        resp.gridIdsToDevices.put(gridId, resp.gridIdsToDevices.get(gridId) + 1);
        resp.totalRegisteredDevices++;
        if (device.getLastHeartbeat() != null && device.getLastHeartbeat() > DateUtils.getPastTime(DateUtils.getMillis(), DateUtils.TimeUnit.Day, 1)) {
          resp.totalActiveDevices++;

        }
      }
    }
    // Send response to client
    out.write(resp.toJson());
  }

  private static void handleEventDetails(final WebSocket.Out<String> out, PublicEventRequest req) {
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    Event event = Event.find().byId(req.pk);
    PublicEventResponse resp = new PublicEventResponse();
    resp.timestamp = DateUtils.toShortDateTime(event.getTimestamp());
    resp.eventType = event.getEventType().getName().split(" ")[0];
    resp.frequency = decimalFormat.format(event.getFrequency());
    resp.voltage = decimalFormat.format(event.getVoltage());
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

  private static void handlePrivateMap(final WebSocket.Out<String> out, PrivateMapRequest req) {
    PublicMapResponse resp = new PublicMapResponse();
    DecimalFormat decimalFormat = new DecimalFormat("#.00");

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
    Integer[] tmpEventMetrics;
    IticRegion region;

    // For filter controls
    long duration;
    long maxDuration = Long.MIN_VALUE;
    double frequency;
    double minFrequency = Double.MAX_VALUE;
    double maxFrequency = Double.MIN_VALUE;
    double voltage;
    double minVoltage = Double.MAX_VALUE;
    double maxVoltage = Double.MIN_VALUE;
    long timestamp;
    long minTimestamp = DateUtils.getPastTime(DateUtils.getMillis(), DateUtils.TimeUnit.Year, 2);
    long maxTimestamp = DateUtils.getMillis();

    // Update event statistics
    Map<String, String> tmpEvent;
    for(Event event : events) {

      // For filter controls
      duration = event.getDuration();
      frequency = event.getFrequency();
      voltage = event.getVoltage();
      timestamp = event.getTimestamp();
      if(duration < 500 && duration > maxDuration) maxDuration = duration;
      if(frequency > 0 && frequency < minFrequency) minFrequency = frequency;
      if(frequency < 200 && frequency > maxFrequency) maxFrequency = frequency;
      if(voltage > 0 && voltage < minVoltage) minVoltage = voltage;
      if(voltage > maxVoltage) maxVoltage = voltage;
      if(timestamp > DateUtils.getPastTime(DateUtils.getMillis(), DateUtils.TimeUnit.Year, 2) && timestamp < minTimestamp) minTimestamp = timestamp;
      if(timestamp < DateUtils.getMillis() && timestamp > maxTimestamp) maxTimestamp = timestamp;
      resp.maxDuration = maxDuration;
      resp.minFrequency = minFrequency;
      resp.maxFrequency = maxFrequency;
      resp.minVoltage = minVoltage;
      resp.maxVoltage = maxVoltage;
      resp.minTimestamp = minTimestamp;
      resp.maxTimestamp = maxTimestamp;

      if(req.containsEvent(event)) {
        // Global metrics
        resp.totalFrequencyEvents = event.getEventType().equals(OpqPacket.PacketType.EVENT_FREQUENCY) ? resp.totalFrequencyEvents + 1 : resp.totalFrequencyEvents;
        resp.totalVoltageEvents = event.getEventType().equals(OpqPacket.PacketType.EVENT_VOLTAGE) ? resp.totalVoltageEvents + 1 : resp.totalVoltageEvents;
        resp.totalEvents++;

        // Grid id to metrics
        gridId = event.getAccessKey().getOpqDevice().getLocation().getGridId().substring(0, gridIdLength);
        if (!resp.gridIdToEventMetrics.containsKey(gridId)) {
          Integer[] z = {0, 0, 0};
          resp.gridIdToEventMetrics.put(gridId, z);
        }
        tmpEventMetrics = resp.gridIdToEventMetrics.get(gridId);
        region = PqUtils.getIticRegion(event.getDuration() * 1000, event.getVoltage());
        tmpEventMetrics[region.severity]++;
        resp.gridIdToEventMetrics.put(gridId, tmpEventMetrics);

        affectedDevices.add(event.getAccessKey().getOpqDevice());
        if (resp.events.size() < MAX_EVENTS) {
          tmpEvent = new HashMap<>();
          tmpEvent.put("id", event.getPrimaryKey().toString());
          tmpEvent.put("timestamp", DateUtils.toDateTime(event.getTimestamp()));
          tmpEvent.put("type", event.getEventType().getName().split(" ")[0]);
          tmpEvent.put("frequency", decimalFormat.format(event.getFrequency()));
          tmpEvent.put("voltage", decimalFormat.format(event.getVoltage()));
          tmpEvent.put("duration", event.getDuration().toString());
          resp.events.add(tmpEvent);
        }
      }
    }
    resp.totalAffectedDevices = affectedDevices.size();

    List<OpqDevice> devices = DbUtils.getAnyLike(OpqDevice.class, "location.gridId", req.visibleIds).findList();
    // Update device statistics
    int i = 0;
    for (OpqDevice device : devices) {
      if (device.getSharingData()) {
        gridId = device.getLocation().getGridId().substring(0, gridIdLength);
        if (!resp.gridIdsToDevices.containsKey(gridId)) {
          resp.gridIdsToDevices.put(gridId, 0);
        }
        resp.gridIdsToDevices.put(gridId, resp.gridIdsToDevices.get(gridId) + 1);
        resp.totalRegisteredDevices++;
        if (device.getLastHeartbeat() != null && device.getLastHeartbeat() > DateUtils.getPastTime(DateUtils.getMillis(), DateUtils.TimeUnit.Day, 1)) {
          resp.totalActiveDevices++;

        }
      }
    }
    // Send response to client
    out.write(resp.toJson());
  }

  private static void handlePrivateEventDetails(final WebSocket.Out<String> out, PrivateEventRequest req) {
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    Event event = Event.find().byId(req.pk);
    PublicEventResponse resp = new PublicEventResponse();
    resp.timestamp = DateUtils.toShortDateTime(event.getTimestamp());
    resp.eventType = event.getEventType().getName().split(" ")[0];
    resp.frequency = decimalFormat.format(event.getFrequency());
    resp.voltage = decimalFormat.format(event.getVoltage());
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
