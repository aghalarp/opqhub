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
import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Event;
import models.OpqDevice;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.GridSquare;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    int idLength = 0;
    List<JsonNode> affectedSquaresJson = new LinkedList<>();


    // Find all visible devices
    idLength = getDevicesFromIds(json, devices);

    Map<String, GridSquare> localMetrics = new HashMap<>();
    GridSquare tmpGridSquare;

    String shortId;
    for (OpqDevice device : devices) {
      shortId = device.getGridId().substring(0, idLength);

      if (!localMetrics.containsKey(shortId)) {
        localMetrics.put(shortId, new GridSquare());
      }

      tmpGridSquare = localMetrics.get(shortId);

      tmpGridSquare.gridId = shortId;
      tmpGridSquare.numDevices++;

      for (Event event : device.getEvents()) {
        switch (event.getEventType()) {
          case EVENT_FREQUENCY:
            tmpGridSquare.numFrequencyEvents++;
            break;
          case EVENT_VOLTAGE:
            tmpGridSquare.numVoltageEvents++;
            break;
        }
      }

      // Calculate total affected devices
      if (device.getEvents().size() > 0) {
        tmpGridSquare.numAffectedDevices++;
      }

    }

    // Calculate global metrics
    int totalAffectedDevices = 0;
    int totalDevices = 0;
    int totalFrequencyEvents = 0;
    int totalVoltageEvents = 0;

    for (String k : localMetrics.keySet()) {
      tmpGridSquare = localMetrics.get(k);
      totalAffectedDevices += tmpGridSquare.numAffectedDevices;
      totalDevices += tmpGridSquare.numDevices;
      totalFrequencyEvents += tmpGridSquare.numFrequencyEvents;
      totalVoltageEvents += tmpGridSquare.numVoltageEvents;
    }


    for (String k : localMetrics.keySet()) {
      affectedSquaresJson.add(formatLocalMetrics(localMetrics.get(k)));
    }

    // Respond with list of affected grid-squares
    ObjectNode result = Json.newObject();
    result.put("affectedSquares", Json.toJson(affectedSquaresJson));
    result.put("globalState", formatGlobalMetrics(totalDevices, totalAffectedDevices, totalFrequencyEvents,
                                                  totalVoltageEvents));

    return ok(result);
  }

  private static JsonNode formatLocalMetrics(GridSquare gridSquare) {
    return Json.parse(String.format(
        "{\"%s\":" +
        "{\"totalDevices\":%d," +
        "\"numAffectedDevices\":%d," +
        "\"numFrequencyEvents\":%d," +
        "\"numVoltageEvents\":%d}}",
        gridSquare.gridId,
        gridSquare.numDevices,
        gridSquare.numAffectedDevices,
        gridSquare.numFrequencyEvents,
        gridSquare.numVoltageEvents
                                   ));
  }

  private static JsonNode formatGlobalMetrics(int totalDevices, int totalAffectedDevices, int totalFrequencyEvents,
                                              int totalVoltageEvents) {
    return Json.parse(String.format("{\"totalDevices\": %d," +
                             "\"totalAffectedDevices\": %d," +
                             "\"totalFrequencyEvents\": %d," +
                             "\"totalVoltageEvents\": %d}",
                             totalDevices,
                             totalAffectedDevices,
                             totalFrequencyEvents,
                             totalVoltageEvents
                            ));
  }

  private static int getDevicesFromIds(JsonNode jsonNode, List<OpqDevice> devices) {
    List<String> partialIds = new LinkedList<>();
    List<String> whereClauses = new LinkedList<>();
    Query<OpqDevice> opqDeviceQuery;
    int idLength = 0;

    for (JsonNode n : jsonNode.findValue("visibleIds")) {
      partialIds.add(n.asText());
      idLength = n.asText().length();
      whereClauses.add("gridId like ?");
    }

    String combinedWhere = StringUtils.join(whereClauses, " or ");
    String qq = "find OpqDevice where " + combinedWhere;

    opqDeviceQuery = Ebean.createQuery(OpqDevice.class, qq);

    int i = 1;
    for (String s : partialIds) {
      opqDeviceQuery.setParameter(i++, s + "%");
    }

    devices.addAll(opqDeviceQuery.findList());

    return idLength;
  }

}
