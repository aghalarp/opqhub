package controllers;

import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.JsonNode;
import json.PublicMapResponse;
import models.Event;
import models.Location;
import models.OpqDevice;
import org.openpowerquality.protocol.OpqPacket;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.DateUtils;
import utils.DbUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicMap extends Controller {
  @Security.Authenticated(Secured.class)
  public static Result displayPublicMap() {
    return ok(views.html.publicmap.render());
  }

  @Security.Authenticated(Secured.class)
  @BodyParser.Of(BodyParser.Json.class)
  public static Result updatePage() {
    JsonNode jsonRequest = request().body().asJson();
    Set<String> gridIds = new HashSet<>();
    Set<Location> locations;
    Query<Location> locationQuery;
    PublicMapResponse jsonResponse = new PublicMapResponse();

    // Get the currently visible grid ids
    for (JsonNode n : jsonRequest.findValue("visibleIds")) {
      gridIds.add(n.asText());
    }

    locationQuery = DbUtils.getAnyLike(Location.class, "gridId", gridIds);
    locations = locationQuery.findSet();
    updateStatistics(jsonResponse, locations);

    return ok(jsonResponse.toString());
  }

  public static void updateStatistics(PublicMapResponse dynamicMapResponse, Set<Location> locations) {
    List<Event> events;
    List<OpqDevice> devices;
    Long activeCutoffTimestamp = DateUtils.getPastTime(DateUtils.getMillis(), DateUtils.TimeUnit.Day);
    Set<Long> affectedDevices = new HashSet<>();

    for(Location location : locations) {
      events = location.getEvents();
      devices = location.getOpqDevices();

      // Update event statistics
      dynamicMapResponse.totalEvents += events.size();
      for(Event event : events) {
        affectedDevices.add(event.getAccessKey().getDeviceId());
        if(event.getEventType().equals(OpqPacket.PacketType.EVENT_FREQUENCY)) {
          dynamicMapResponse.totalFrequencyEvents++;
        }
        if(event.getEventType().equals(OpqPacket.PacketType.EVENT_VOLTAGE)) {
          dynamicMapResponse.totalVoltageEvents++;
        }
      }

      // Update device statistics
      dynamicMapResponse.totalRegisteredDevices += devices.size();
      dynamicMapResponse.totalAffectedDevices = affectedDevices.size();
      for(OpqDevice device : devices) {
        if(device.getLastHeartbeat() > activeCutoffTimestamp) {
          dynamicMapResponse.totalActiveDevices++;
        }
      }
    }
  }
}
