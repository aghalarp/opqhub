package controllers;

import models.Event;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class PublicMonitor extends Controller {
  public static Result publicMonitor() {
    // Find default values and call method using those default values.
    double minFrequency = Event.getMinFrequency();
    double maxFrequency = Event.getMaxFrequency();
    double minVoltage = Event.getMinVoltage();
    double maxVoltage = Event.getMaxVoltage();
    int minDuration = (int) Event.getMinDuration();
    int maxDuration = (int) Event.getMaxDuration();
    long minTimestamp = Event.getMinTimestamp();
    long maxTimestamp = Event.getMaxTimestamp();
    double mapCenterLat = 21.466700;
    double mapCenterLng = -157.983300;
    int mapZoom = 8;
    String mapVisibleIds = "0,0:0;0,0:1;0,1:0;0,1:1;0,2:0;0,2:1;0,3:0;0,3:1;0,0:3;0,0:2;" +
      "0,1:3;0,1:2;0,2:3;0,2:2;0,3:3;0,3:2;1,0:0;1,0:1;1,1:0;1,1:1;1,2:0;1,2:1;1,3:0;1,3:1;1,0:3;1,0:2;1,1:3;" +
      "1,1:2;1,2:3;1,2:2;1,3:3;1,3:2;2,0:0;2,0:1;2,1:0;2,1:1;2,2:0;2,2:1;2,3:0;2,3:1;2,4:0";


    System.out.println(String.format("%f %f %f %f %d %d %d %d\n", minFrequency, maxFrequency, minVoltage, maxVoltage,
                                     minDuration, maxDuration, minTimestamp, maxTimestamp));

    return redirect(routes.PublicMonitor.publicMonitorWithArgs(minFrequency, maxFrequency, minVoltage, maxVoltage, minDuration, maxDuration, minTimestamp, maxTimestamp, mapCenterLat, mapCenterLng, mapZoom, mapVisibleIds));
  }

  public static Result publicMonitorWithArgs(double minFrequency, double maxFrequency, double minVoltage, double maxVoltage,
                                     int minDuration, int maxDuration, long minTimestamp, long maxTimestamp,
                                     double mapCenterLat, double mapCenterLng, int mapZoom, String mapVisibleIds) {

    List<String> visibleIdList = Arrays.asList(mapVisibleIds.split(Pattern.quote(";")));
    List<Event> events = Event.getPublicEvents(minFrequency, maxFrequency, minVoltage, maxVoltage, minDuration,
      maxDuration, minTimestamp, maxTimestamp, true, true, true, true, true, true, visibleIdList);

    return ok(views.html.publicmonitor.render(events));
  }
}
