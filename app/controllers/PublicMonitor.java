package controllers;

import models.Event;
import org.openpowerquality.protocol.OpqPacket;
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

    mapVisibleIds = mapVisibleIds
        .replaceAll(Pattern.quote(","), "c")
        .replaceAll(Pattern.quote(":"), "C")
        .replaceAll(Pattern.quote(";"), "s");


    return redirect(routes.PublicMonitor.publicMonitorWithArgs(true, minFrequency, maxFrequency, true, minVoltage, maxVoltage,
      minDuration, maxDuration, minTimestamp, maxTimestamp, true, true, true, mapCenterLat, mapCenterLng, mapZoom, new Integer(0), mapVisibleIds));
  }

  public static Result publicMonitorWithArgs(boolean requestFrequency, double minFrequency, double maxFrequency, boolean requestVoltage, double minVoltage, double maxVoltage,
                                     int minDuration, int maxDuration, long minTimestamp, long maxTimestamp, boolean iticSevere, boolean iticModerate, boolean iticOk,
                                     double mapCenterLat, double mapCenterLng, int mapZoom, int page, String mapVisibleIds) {
    String replacedVisibleIds = mapVisibleIds
        .replaceAll(Pattern.quote("c"), ",")
        .replaceAll(Pattern.quote("C"), ":")
        .replaceAll(Pattern.quote("s"), ";");

    List<String> visibleIdList = Arrays.asList(replacedVisibleIds.split(Pattern.quote(";")));
    List<Event> totalEvents = Event.getPublicEvents(minFrequency, maxFrequency, minVoltage, maxVoltage, minDuration,
                                                    maxDuration, minTimestamp, maxTimestamp, iticSevere, iticModerate, iticOk, requestFrequency, requestVoltage, true, visibleIdList);

    long totalEventsCount = totalEvents.size();
    long voltageEventsCount = totalEvents.parallelStream()
      .filter(evt -> evt.getEventType().equals(OpqPacket.PacketType.EVENT_VOLTAGE)).count();
    long frequencyEventsCount = totalEventsCount - voltageEventsCount;

    List<Event> pagedEvents = Event.getPublicEvents(minFrequency, maxFrequency, minVoltage, maxVoltage, minDuration,
      maxDuration, minTimestamp, maxTimestamp, iticSevere, iticModerate, iticOk, requestFrequency, requestVoltage, true, visibleIdList, page);

    return ok(views.html.publicmonitor.render(pagedEvents, totalEventsCount, frequencyEventsCount, voltageEventsCount,
                                              mapCenterLat, mapCenterLng, mapZoom, requestFrequency,
                                              minFrequency, maxFrequency,
                                              requestVoltage, minVoltage, maxVoltage, minDuration, maxDuration,
                                              minTimestamp, maxTimestamp, iticSevere, iticModerate, iticOk));
  }
}
