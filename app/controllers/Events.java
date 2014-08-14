package controllers;

import com.avaje.ebean.Query;
import models.AccessKey;
import models.Event;
import models.Person;
import models.Location;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.DateUtils;
import utils.DbUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Events extends Controller {
  @Security.Authenticated(Secured.class)
  public static Result filterEvents() {
    DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
    String selectedTimeUnit = dynamicForm.get("pastTimeSelect");

    Long adjustedTimestamp = DateUtils.getMillis() - DateUtils.TimeUnit.valueOf(selectedTimeUnit).getMilliseconds();
    session("pastTimeSelectEvents", selectedTimeUnit);
    session("eventsAfterAmount", adjustedTimestamp.toString());
    return redirect(controllers.routes.Events.eventsByPage(0, adjustedTimestamp));
  }

  @Security.Authenticated(Secured.class)
  public static Result eventsByPage(Integer page, Long afterTimestamp) {
    final Integer ROWS_PER_PAGE = 10;
    Long after = (afterTimestamp == null) ? 0 : afterTimestamp;
    Person person = Person.getLoggedIn();
    Set<String> keyValues = new HashSet<>();

    for(AccessKey key : person.getAccessKeys()) {
      keyValues.add(key.getPrimaryKey().toString());
    }

    Query<Event> query = DbUtils.getAnyLike(Event.class, "accessKey.primaryKey", keyValues);

    List<Event> events = query
        .where()
        .gt("timestamp", after)
        .order("timestamp desc")
        .findPagingList(ROWS_PER_PAGE)
        .getPage(page)
        .getList();

    Integer pages = query.where().gt("timestamp", after).findRowCount() / ROWS_PER_PAGE;

    return ok(views.html.privatemonitoring.privateevents.render(events, page, pages));
  }

  @Security.Authenticated(Secured.class)
  public static Result eventDetails(Long eventId) {
    Event event = Event.find().where().eq("primaryKey", eventId).findUnique();
    Location location = event.getLocation();
    String waveform = event.getEventData().getWaveform();

    return ok(views.html.privatemonitoring.eventdetails.render(event, location, waveform));
  }

  public static Result rawPowerData(Long eventId) {
    return ok(Event.find().byId(eventId).getEventData().getWaveform());
  }

  @Security.Authenticated(Secured.class)
  public static Result filterNearbyEvents() {
    DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
    String selectedTimeUnit = dynamicForm.get("pastTimeSelect");
    Long deviceId = Long.parseLong(dynamicForm.get("deviceId"));

    Long adjustedTimestamp = DateUtils.getMillis() - DateUtils.TimeUnit.valueOf(selectedTimeUnit).getMilliseconds();
    session("pastTimeSelectNearby", selectedTimeUnit);
    session("nearbyEventsAfterAmount", adjustedTimestamp.toString());
    return redirect(routes.Events.nearbyEventsByPage(deviceId, 0, adjustedTimestamp));
  }

  @Security.Authenticated(Secured.class)
  public static Result nearbyEvents() {
    return TODO;
    // Find the first device associated with this person
//    OpqDevice device = OpqDevice.find().where()
//                                .eq("person.email", session("email"))
//                                .findList()
//                                .get(0);
//
//    if(device == null) {
//      Logger.warn(String.format("Could not locate device associated with [%s] for nearby events", session("email")));
//      return ok(error.render("Could not locate device with id", session("email")));
//    }
//
//    return redirect(routes.Events.nearbyEventsByPage(device.getDeviceId(), 0, 0L));
  }

  @Security.Authenticated(Secured.class)
  public static Result nearbyEventsByPage(Long deviceId, Integer page, Long afterTimestamp) {
    return TODO;
//    final int PAGE_SIZE = 10;
//    OpqDevice device = OpqDevice.find().where()
//                                .eq("deviceId", deviceId)
//                                .findUnique();
//
//    if(device == null) {
//      Logger.warn(String.format("Could not locate device associated with [%s] for nearby events", session("email")));
//      return ok(error.render("Could not locate device with id", deviceId.toString()));
//    }
//
//    // Get the grid square that the device is associated with
//    String gridId = device.getLocation().getGridId();
//
//    if(gridId == null || !device.getSharingData()) {
//      return ok(error.render("Please make sure you've set your preferences to allow data sharing", ""));
//    }
//
//    // TODO: Get rid of all events that are attached to any of the user's devices
//    double scale = device.getLocation().getGridScale();
//    int cnt = 0;
//
//    while(scale < 4) {
//      cnt++;
//      scale *= 2;
//    }
//
//    Long after = (afterTimestamp == null) ? 0 : afterTimestamp;
//    List<Event> events = Event.find().where()
//                              .startsWith("device.gridId", gridId.substring(0, gridId.length() - cnt))
//                              .ne("device.deviceId", deviceId)
//                              .eq("device.sharingData", true)
//                              .gt("timestamp", after)
//                              .order("timestamp desc")
//                              .findPagingList(PAGE_SIZE)
//                              .getPage(page).getList();

//    return ok(views.html.privatemonitoring.nearbyevents.render(events,
//                                                               page,
//                                                               (events.size() / PAGE_SIZE),
//                                                               deviceId,
//                                                               device.getLocation().getGridScale()));
  }
}
