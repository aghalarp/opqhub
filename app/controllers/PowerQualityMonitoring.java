/*
  This file is part of opq-ao.

  opa-ao is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opa-ao is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opq-ao.  If not, see <http://www.gnu.org/licenses/>.

  Copyright 2013 Anthony Christe
 */

package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Alert;
import org.apache.commons.lang3.StringUtils;
import org.openpowerquality.protocol.OpqPacket;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import models.Event;
import models.OpqDevice;
import scala.util.parsing.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains methods for modifying views and models for both private and public power quality monitoring.
 */
public class PowerQualityMonitoring extends Controller {

  /**
   * Render a public "google-maps" style map with devices and alerts from users who are participating in data sharing.
   *
   * @return Rendered view of publics' devices and public alerts.
   */
  public static Result publicMonitor() {
    List<Event> events = Event.find().all();
    List<models.OpqDevice> devices = OpqDevice.find().where().eq("sharingData", true).findList();
    boolean loggedOut = !(session().containsKey("email"));
    return ok(views.html.publicmonitoring.publicpowerqualitymonitoring.render(events, devices, loggedOut));
  }

  @BodyParser.Of(BodyParser.Json.class)
  public static Result alertsFromIds() {
    JsonNode json = request().body().asJson();
    List<OpqDevice> devices = new LinkedList<>();
    Map<String, Set<Event>> squareToEvents = new HashMap<>();
    Map<String, Integer> gridIdToNumDevices = new HashMap<>();
    int idLength = 0;
    List<JsonNode> affectSquaresJson = new LinkedList<>();

    // Find devices
    idLength = getDevicesFromIds(json, devices);
    String shortId;
    for(OpqDevice device : devices) {
      shortId = device.getGridId().substring(0, idLength);
      if(!gridIdToNumDevices.containsKey(shortId)) {
        gridIdToNumDevices.put(shortId, 0);
      }
      gridIdToNumDevices.put(shortId, gridIdToNumDevices.get(shortId) + 1);
    }

    int totalAffectedDevices = 0;

    //Find events associated with devices
    for(OpqDevice device : devices) {
      if(device.getEvents().size() > 0) {
        totalAffectedDevices++;
        shortId = device.getGridId().substring(0, idLength);
        if(!squareToEvents.containsKey(shortId)) {
            squareToEvents.put(shortId, new HashSet<Event>());
        }
        squareToEvents.get(shortId).addAll(device.getEvents());
      }
    }

    // Calculate global metrics
    int totalDevices = devices.size();
    int totalFrequencyEvents = 0;
    int totalVoltageEvents = 0;

    for(String s : squareToEvents.keySet()) {
      for(Event event : squareToEvents.get(s)) {
        if(event.getEventType().equals(OpqPacket.PacketType.EVENT_FREQUENCY)) {
          totalFrequencyEvents++;
        }
        else if(event.getEventType().equals(OpqPacket.PacketType.EVENT_VOLTAGE)) {
          totalVoltageEvents++;
        }
      }
    }

    for(String k : squareToEvents.keySet()) {
      affectSquaresJson.add(formatGridPopup(k, squareToEvents.get(k), gridIdToNumDevices));
    }

    // Respond with list of affected grid-squares
    ObjectNode result = Json.newObject();
    result.put("affectedSquares", Json.toJson(affectSquaresJson));
    result.put("globalState", Json.parse(String.format("{\"totalDevices\": %d," +
                                                       "\"totalAffectedDevices\": %d," +
                                                       "\"totalFrequencyEvents\": %d," +
                                                       "\"totalVoltageEvents\": %d}",
                                                       totalDevices,
                                                       totalAffectedDevices,
                                                       totalFrequencyEvents,
                                                       totalVoltageEvents)));

    return ok(result);
  }

  private static int getDevicesFromIds(JsonNode jsonNode, List<OpqDevice> devices) {
    List<String> partialIds = new LinkedList<>();
    List<String> whereClauses = new LinkedList<>();
    Query<OpqDevice> opqDeviceQuery;
    int idLength = 0;

    for(JsonNode n : jsonNode.findValue("visibleIds")) {
      partialIds.add(n.asText());
      idLength = n.asText().length();
      whereClauses.add("gridId like ?");
    }

    String combinedWhere = StringUtils.join(whereClauses, " or ");
    String qq = "find OpqDevice where " + combinedWhere;

    opqDeviceQuery = Ebean.createQuery(OpqDevice.class, qq);

    int i = 1;
    for(String s : partialIds) {
      opqDeviceQuery.setParameter(i++, s + "%");
    }

    devices.addAll(opqDeviceQuery.findList());

    return idLength;
  }



  private static JsonNode formatGridPopup(String gridId, Set<Event> events, Map<String, Integer> gridIdToDevices) {
    Set<OpqDevice> devices = new HashSet<>();
    int numAffectedDevices = 0;
    int numFrequencyEvents = 0;
    int numVoltageEvents = 0;

    for(Event event : events) {
      devices.add(event.getDevice());
      if(event.getEventType().equals(OpqPacket.PacketType.EVENT_FREQUENCY)) {
        numFrequencyEvents++;
      }
      else if(event.getEventType().equals(OpqPacket.PacketType.EVENT_VOLTAGE)) {
        numVoltageEvents++;
      }
    }
    numAffectedDevices = devices.size();
    JsonNode result = Json.parse(String.format("{\"%s\":" +
                                               "{\"totalDevices\":%d," +
                                               "\"numAffectedDevices\":%d," +
                                               "\"numFrequencyEvents\":%d," +
                                               "\"numVoltageEvents\":%d}}",
                                               gridId, gridIdToDevices.get(gridId), numAffectedDevices, numFrequencyEvents, numVoltageEvents));
    return result;

  }

}
