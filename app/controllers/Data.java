package controllers;

import models.Event;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DateUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Data extends Controller {
  public static Result exportData() {
    Long from = 1407060000000L;                            // Aug 3 midnight Aug 4 1159
    Long to = 1407232800000L;
    List<Event> events = Event.find().where()
        .lt("timestamp", to)
        .gt("timestamp", from)
        .findList();

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


    BufferedWriter out;
    BufferedWriter dataOut;
    for(String d : descriptionToEvents.keySet()) {
      try {
        out = new BufferedWriter(new FileWriter("/opt/opq/data/" + d + ".txt"));
        for (Event event : descriptionToEvents.get(d)) {
          dataOut = new BufferedWriter(new FileWriter("/opt/opq/data/" + event.getPrimaryKey() + ".txt"));
          dataOut.write(event.getEventData().getWaveform());
          dataOut.flush();
          dataOut.close();
          out.append(String
                         .format("%s,%s,%s,%s,%s,%s,%s\n", d, event.getEventType(), event.getTimestamp(), event.getFrequency(), event.getVoltage(),
                                 event.getDuration(), event.getPrimaryKey() + ".txt"));
        }
        out.flush();
        out.close();
      }
      catch(IOException e) {
        e.printStackTrace();
      }
    }

    return TODO;
  }
}
