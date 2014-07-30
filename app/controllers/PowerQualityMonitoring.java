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
import java.util.Iterator;
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

  /**
   * Finds alerts associated with given list of visible grid-square ids.
   *
   * This method receives a json array of currently visible grid-squares on the map and then finds all events associated
   * with those grid-squares and sends back a json response with an array of affected squares and other various metrics.
   *
   * @return A json response of affected squares and metrics for current visible area.
   */
  @BodyParser.Of(BodyParser.Json.class)
  public static Result alertsFromIds() {
    List<OpqDevice> devices = new LinkedList<>();
    JsonNode json = request().body().asJson();
    int idLength = getDevicesFromIds(json, devices);
    return ok(formatJsonResult(calculateLocalMetrics(devices, idLength, json)));
  }

  /**
   * Calculates local metrics for each grid square.
   * @param devices Visible devices.
   * @param idLength Length of ids at current grid scale.
   * @return Local metrics.
   */
  private static Map<String, GridSquare> calculateLocalMetrics(List<OpqDevice> devices, int idLength, JsonNode json) {
    Map<String, GridSquare> localMetrics = new HashMap<>();
    String shortId;
    GridSquare tmpGridSquare;
    List<Event> events;
    Long afterTimestamp;
    Long beforeTimestamp;

    for (OpqDevice device : devices) {
      // We want to use the truncated grid id that corresponds to the current zoom level
      shortId = device.getLocation().getGridId().substring(0, idLength);

      if (!localMetrics.containsKey(shortId)) {
        localMetrics.put(shortId, new GridSquare());
      }

      tmpGridSquare = localMetrics.get(shortId);

      tmpGridSquare.gridId = shortId;
      tmpGridSquare.numDevices++;

      // Get events associated with devices

      events = device.getAccessKey().getEvents();
      afterTimestamp = json.findValue("after").asLong();
      beforeTimestamp = json.findValue("before").asLong();

      Iterator<Event> eventIterator = events.iterator();
      Event tmpEvent;

      while(eventIterator.hasNext()) {
        tmpEvent = eventIterator.next();
        if(tmpEvent.getTimestamp() < afterTimestamp || tmpEvent.getTimestamp() > beforeTimestamp) {
          eventIterator.remove();
        }

      }

      // Calculate total affected devices
      if (events.size() > 0) {
        tmpGridSquare.numAffectedDevices++;
      }

      // Calculate number of freq, voltage events
      for (Event event : events) {
        switch (event.getEventType()) {
          case EVENT_FREQUENCY:
            tmpGridSquare.numFrequencyEvents++;
            break;
          case EVENT_VOLTAGE:
            tmpGridSquare.numVoltageEvents++;
            tmpGridSquare.addIticPoint(event.getDuration(), event.getVoltage());
            break;
          default:
            break;
        }
      }
    }
    return localMetrics;
  }

  /**
   * Find all opq devices associated with list of visible grid-squares.
   *
   * This method makes use of Ebeans query language so that all of the devices can be found in a single
   * query. The queries have to be chained this way because we use 'a OR b OR c OR ... etc'. This allows for a dynamic
   * number of ids to be present in the query.
   *
   * @param jsonNode Contains an array of currently visible grid-squares.
   * @param devices This is used as a reference variable and the resulting devices will be returned through this
   *                parameter.
   * @return        The length of the grid-ids found in the json array.
   */
  private static int getDevicesFromIds(JsonNode jsonNode, List<OpqDevice> devices) {
    List<String> partialIds = new LinkedList<>();
    List<String> likeClauses = new LinkedList<>();
    Query<OpqDevice> query;
    int idLength = 0;

    // Retrieve the partial ids and create enough like clauses for each id
    for (JsonNode n : jsonNode.findValue("visibleIds")) {
      partialIds.add(n.asText());
      idLength = n.asText().length();
      likeClauses.add("gridId like ?");
    }


    String queryString = "find OpqDevice where " + StringUtils.join(likeClauses, " or ");

    query = Ebean.createQuery(OpqDevice.class, queryString);

    // Note: query parameters start at index 1, not 0!
    int i = 1;
    for (String s : partialIds) {
      query.setParameter(i++, s + "%");
    }

    devices.addAll(query.findList());

    return idLength;
  }

  /**
   * Formats the json response for alertsFromIds.
   *
   * This method packaged together the local metrics for each grid square as well as the global metrics for the entire
   * viewable area.
   *
   * @param metrics Metrics local to each grid-square.
   * @return A Json object which contains both local and global metrics.
   */
  private static ObjectNode formatJsonResult(Map<String, GridSquare> metrics) {
    int totalAffectedDevices = 0;
    int totalDevices = 0;
    int totalFrequencyEvents = 0;
    int totalVoltageEvents = 0;
    List<JsonNode> affectedSquares = new LinkedList<>();

    GridSquare tmpGridSquare;

    // Tally global metrics
    for (String k : metrics.keySet()) {
      tmpGridSquare = metrics.get(k);
      totalAffectedDevices += tmpGridSquare.numAffectedDevices;
      totalDevices += tmpGridSquare.numDevices;
      totalFrequencyEvents += tmpGridSquare.numFrequencyEvents;
      totalVoltageEvents += tmpGridSquare.numVoltageEvents;

      // Find affected squares with events
      if(tmpGridSquare.numAffectedDevices > 0) {
        affectedSquares.add(formatLocalMetrics(tmpGridSquare));
      }
    }

    // Format as json
    ObjectNode result = Json.newObject();
    result.put("affectedSquares", Json.toJson(affectedSquares));
    result.put("globalState", formatGlobalMetrics(totalDevices, totalAffectedDevices, totalFrequencyEvents,
                                                  totalVoltageEvents));

    return result;
  }

  /**
   * Format the local metrics for each grid square into a json node.
   * @param gridSquare The grid square to format.
   * @return Formatted json node from given grid square.
   */
  private static JsonNode formatLocalMetrics(GridSquare gridSquare) {
    StringBuilder iticPoints = new StringBuilder("[");
    for(GridSquare.IticPoint iticPoint : gridSquare.iticPoints) {
      iticPoints.append(iticPoint);
      iticPoints.append(",");
    }
    iticPoints.deleteCharAt(iticPoints.lastIndexOf(","));
    iticPoints.append("]");


    return Json.parse(String.format(
        "{\"%s\":" +
        "{\"totalDevices\":%d," +
        "\"numAffectedDevices\":%d," +
        "\"numFrequencyEvents\":%d," +
        "\"numVoltageEvents\":%d,"+
        "\"iticPoints\":%s}}",
        gridSquare.gridId,
        gridSquare.numDevices,
        gridSquare.numAffectedDevices,
        gridSquare.numFrequencyEvents,
        gridSquare.numVoltageEvents,
        iticPoints.toString()));
  }

  /**
   * Format the global metrics as a JsonNode.
   * @param totalDevices Total number of devices.
   * @param totalAffectedDevices Total number of affected devices.
   * @param totalFrequencyEvents Total number of frequency events.
   * @param totalVoltageEvents Total number of voltage events.
   * @return Formatted global metrics.
   */
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
}
