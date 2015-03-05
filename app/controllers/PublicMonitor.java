package controllers;

import models.Event;
import play.mvc.Controller;
import play.mvc.Result;


public class PublicMonitor extends Controller {
  public static Result publicMonitor() {
    // Find default values and call method using those default values.
    double minFrequency = Event.getMinFrequency();
    double maxFrequency = Event.getMaxFrequency();
    double minVoltage = Event.getMinVoltage();
    double maxVoltage = Event.getMaxVoltage();
    long minDuration = Event.getMinDuration();
    long maxDuration = Event.getMaxDuration();
    long minTimestamp = Event.getMinTimestamp();
    long maxTimestamp = Event.getMaxTimestamp();

    // Default map view


    System.out.println(String.format("%f %f %f %f %d %d %d %d\n", minFrequency, maxFrequency, minVoltage, maxVoltage,
                                     minDuration, maxDuration, minTimestamp, maxTimestamp));

    /*
    String idStr = "0,0:0;0,0:1;0,1:0;0,1:1;";//0,2:0;0,2:1;0,3:0;0,3:1;0,0:3;0,0:2;0,1:3;0,1:2;0,2:3;0,2:2;0,3:3;0,3:2;1,0:0;1,0:1;1,1:0;1,1:1;1,2:0;1,2:1;1,3:0;1,3:1;1,0:3;1,0:2;1,1:3;1,1:2;1,2:3;1,2:2;1,3:3;1,3:2;2,0:0;2,0:1;2,1:0;2,1:1;2,2:0;2,2:1;2,3:0;2,3:1;2,4:0";
    List<String> gridIds = Arrays.asList("1,2:1");
    // We only want to get at events inside of the current map error
    List<String> gridIdBeginWiths = gridIds.stream()
                                           .map(gridId -> DbUtils.beginsWith("location.gridId", gridId))
                                           .collect(Collectors.toList());
    String locations = DbUtils.or(gridIdBeginWiths);

    // Only look for devices that are sharing data
    String sharing = DbUtils.eq("access_key.opq_device.sharing_data", 1L);

    // Build the sql query
    String sql = String.format("SELECT FROM event WHERE %s " + // Sharing data
                               "AND %s;" // locations
        ,sharing, locations);
    return ok(((Integer) Event.getPublicEvents(58, 62, 20, 200, 0, 500, 0, Long.MAX_VALUE, false, true, true, true, false, true, gridIds).size()).toString());
    */
    return TODO;
  }
}
