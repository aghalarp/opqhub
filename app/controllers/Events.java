package controllers;

import models.Event;
import models.ExternalCause;
import models.OpqDevice;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.DateUtils;
import views.html.error;

import java.util.List;

public class Events extends Controller {
  @Security.Authenticated(Secured.class)
  public static Result filterEvents() {
    DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
    String selectedTimeUnit = dynamicForm.get("pastTimeSelect");

    Long adjustedTimestamp = utils.DateUtils.getMillis() - DateUtils.TimeUnit.valueOf(selectedTimeUnit).getMilliseconds();
    session("pastTimeSelectEvents", selectedTimeUnit);
    return redirect(routes.Events.eventsByPage(0, adjustedTimestamp));
  }

  @Security.Authenticated(Secured.class)
  public static Result eventsByPage(Integer page, Long afterTimestamp) {
    Integer pages;
    final Integer ROWS_PER_PAGE = 10;
    Long after = (afterTimestamp == null) ? 0 : afterTimestamp;
    List<Event> events = Event.find().where()
        .eq("device.person.email", session("email"))
        .gt("timestamp", after)
        .order("timestamp desc")
        .findPagingList(ROWS_PER_PAGE)
        .getPage(page)
        .getList();

    pages = Event.find().where().eq("device.person.email", session("email")).gt("timestamp", after).findRowCount() / ROWS_PER_PAGE;

    return ok(views.html.privatemonitoring.privateevents.render(events, page, pages));
  }

  @Security.Authenticated(Secured.class)
  public static Result eventDetails(Long eventId) {
    Event event = Event.find().where().eq("primaryKey", eventId).findUnique();
    ExternalCause externalCause = event.getExternalCause();
    Form<ExternalCause> externalEventForm;

    // TODO: Error page for when event is not found

    if(externalCause == null) {
      externalEventForm = Form.form(ExternalCause.class);
    }
    else {
      externalEventForm = Form.form(ExternalCause.class).fill(externalCause);
    }

    return ok(views.html.privatemonitoring.eventdetails.render(event, externalEventForm));
  }

  @Security.Authenticated(Secured.class)
  public static Result updateEventDetails(Long eventId) {
    Event event = Event.find().where().eq("primaryKey", eventId).findUnique();
    Form<ExternalCause> externalEventForm = Form.form(ExternalCause.class).bindFromRequest();

    // TODO: Error page for when event is not found

    if (externalEventForm.hasErrors()) {
      Logger.debug(String.format("Could not update event [%s] details due to %s", event.getPrimaryKey(),
                                 externalEventForm.errors().toString()));
      return ok(error.render("Problem updating event", externalEventForm.errors().toString()));
    }

    ExternalCause externalCause = externalEventForm.get();
    externalCause.getEvents().add(event);
    event.setExternalCause(externalCause);
    externalCause.save();
    event.save();

    flash("updated", "External Cause Updated");
    Logger.debug(String.format("Event [%s] details updated", event.getPrimaryKey()));
    return redirect(routes.Events.eventDetails(eventId));
  }

  @Security.Authenticated(Secured.class)
  public static Result nearbyEvents() {
    // Find the first device associated with this person
    OpqDevice device = OpqDevice.find().where()
                                .eq("person.email", session("email"))
                                .findList()
                                .get(0);

    if(device == null) {
      Logger.warn(String.format("Could not locate device associated with [%s] for nearby events", session("email")));
      return ok(error.render("Could not locate device with id", session("email")));
    }

    return redirect(routes.Events.nearbyEventsByPage(device.getDeviceId(), 0, 0L));
  }

  @Security.Authenticated(Secured.class)
  public static Result nearbyEventsByPage(Long deviceId, Integer page, Long afterTimestamp) {
    final int PAGE_SIZE = 10;
    OpqDevice device = OpqDevice.find().where()
                                .eq("deviceId", deviceId)
                                .findUnique();

    if(device == null) {
      Logger.warn(String.format("Could not locate device associated with [%s] for nearby events", session("email")));
      return ok(error.render("Could not locate device with id", deviceId.toString()));
    }

    // Get the grid square that the device is associated with
    String gridId = device.getGridId();

    if(gridId == null || !device.getSharingData()) {
      return ok(error.render("Please make sure you've set your preferences to allow data sharing", ""));
    }

    // TODO: Get rid of all events that are attached to any of the user's devices
    double scale = device.getGridScale();
    int cnt = 0;

    while(scale < 4) {
      cnt++;
      scale *= 2;
    }

    Long after = (afterTimestamp == null) ? 0 : afterTimestamp;
    List<Event> events = Event.find().where()
                              .startsWith("device.gridId", gridId.substring(0, gridId.length() - cnt))
                              .ne("device.deviceId", deviceId)
                              .eq("device.sharingData", true)
                              .gt("timestamp", after)
                              .order("timestamp desc")
                              .findPagingList(PAGE_SIZE)
                              .getPage(page).getList();

    return ok(views.html.privatemonitoring.nearbyevents.render(events,
                                                               page,
                                                               (events.size() / PAGE_SIZE),
                                                               deviceId,
                                                               device.getGridScale()));
  }
}
