package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import json.Trend;
import json.Trends;
import models.AccessKey;
import models.Event;
import models.OpqDevice;
import models.Person;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.DateUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Data extends Controller {
  @Security.Authenticated(SecuredAndMatched.class)
  public static Result getEvents(String email, Long timestampGt, Long timestampLt) {
    Person person = Person.find().where().eq("email", email).findUnique();
    Set<AccessKey> accessKeys = person.getAccessKeys();
    List<Event> events = new ArrayList<>();
    for(AccessKey accessKey : accessKeys) {
      for(Event event : accessKey.getEvents()) {
        Long timestamp = event.getTimestamp();
        if(timestamp > timestampGt && timestamp < timestampLt) {

          events.add(event);
        }
      }
    }

    Map<String, List<Event>> descriptionToEvents = new HashMap<>();
    String description;

    // Sort events by location description
    for(Event event : events) {
      description = event.getAccessKey().getOpqDevice().getDescription();
      if(!descriptionToEvents.containsKey(description)) {
        descriptionToEvents.put(description, new LinkedList<Event>());
      }
      descriptionToEvents.get(description).add(event);
    }

    String fileLocation = "/tmp/opqhub_" + UUID.randomUUID().toString() + ".csv";

    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(fileLocation));
      for (String d : descriptionToEvents.keySet()) {

        for (Event event : descriptionToEvents.get(d)) {
          out.append(String
                         .format("%s,%s,%s,%s,%s,%s,%s,%s\n", event.getPrimaryKey(), d, event.getEventType(),
                                 event.getTimestamp(), DateUtils.toDateTime(event.getTimestamp()), event.getFrequency(), event.getVoltage(),
                                 event.getDuration()));
        }

      }
      out.flush();
      out.close();
    }
    catch (IOException e) {

    }
    return ok(new java.io.File(fileLocation));
  }

  @Security.Authenticated(SecuredAndMatched.class)
  public static Result getWaveform(String email, Long eventPrimaryKey) {
    Person person = Person.find().where().eq("email", email).findUnique();
    Event event = Event.find().byId(eventPrimaryKey);

    if(!event.getAccessKey().getPersons().contains(person)) {
      return unauthorized("You do not have access to this data");
    }

    if(event == null) {
      return notFound("Data not found");
    }
    String fileLocation = "/tmp/opqhub_" + UUID.randomUUID().toString() + ".csv";

    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(new File(fileLocation)));
      out.write(event.getEventData().getWaveform());
      out.flush();
      out.close();
    }
    catch(IOException e) {

    }
    return ok(new java.io.File(fileLocation));
  }

  @Security.Authenticated(SecuredAndMatched.class)
  @BodyParser.Of(BodyParser.Json.class)
  public static Result getTrends(String email, Long timestampGt, Long timestampLt) {
      Person person = Person.find().where().eq("email", email).findUnique();
      Set<AccessKey> accessKeys = person.getAccessKeys();
      Map<OpqDevice, ArrayList<Double>> deviceToVoltageTrends = new HashMap<>();
      OpqDevice tmpDevice;
      List<Event> events;
      Long timestamp;

      // Get and filter the data on the timestamps
      for(AccessKey accessKey : accessKeys) {
          tmpDevice = accessKey.getOpqDevice();
          if(!deviceToVoltageTrends.containsKey(tmpDevice)) {
              deviceToVoltageTrends.put(tmpDevice, new ArrayList<Double>());
          }
          events = accessKey.getEvents();
          for(Event event : events) {
              timestamp = event.getTimestamp();
              if(timestamp != null && timestamp > timestampGt && timestamp < timestampLt && event.getVoltage() > 0) {
                  deviceToVoltageTrends.get(tmpDevice).add(timestamp.doubleValue());
                  deviceToVoltageTrends.get(tmpDevice).add(event.getVoltage());
              }
          }
      }

      Trends trends = new Trends();
      // Transform into json
      for(OpqDevice device : deviceToVoltageTrends.keySet()) {
          if(deviceToVoltageTrends.get(device).size() > 0) {
              trends.add(new Trend(device.getDeviceId(), device.getDescription(), deviceToVoltageTrends.get(device)));
          }
      }

      return ok(Json.parse(trends.toJson()));
  }

}

