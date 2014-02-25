package controllers;

import com.avaje.ebean.Expression;
import com.avaje.ebean.ExpressionList;
import models.Alert;
import models.ExternalEvent;
import models.OpqDevice;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.DateUtils;
import views.html.error;

import java.util.List;

public class Events extends Controller {

  public static Result filterEvents() {
    DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
    String selectedTimeUnit = dynamicForm.get("pastTimeSelect");

    Long adjustedTimestamp = utils.DateUtils.getMillis() - DateUtils.TimeUnit.valueOf(selectedTimeUnit).getMilliseconds();
    flash("pastTimeSelect", selectedTimeUnit);
    return redirect(routes.Events.eventsByPage(0, adjustedTimestamp));
  }

  public static Result eventsByPage(Integer page, Long afterTimestamp) {
    Integer pages;
    final Integer ROWS_PER_PAGE = 10;
    Long after = (afterTimestamp == null) ? 0 : afterTimestamp;
    List<Alert> events = Alert.find().where()
        .eq("device.person.email", session("email"))
        .gt("timestamp", after)
        .order("timestamp desc")
        .findPagingList(ROWS_PER_PAGE)
        .getPage(page)
        .getList();

    pages = Alert.find().where().eq("device.person.email", session("email")).gt("timestamp", after).findRowCount() / ROWS_PER_PAGE;

    return ok(views.html.privatemonitoring.privateevents.render(events, page, pages));
  }

  @Security.Authenticated(Secured.class)
  public static Result eventDetails(Long eventId) {
    Alert event = Alert.find().where().eq("primaryKey", eventId).findUnique();
    ExternalEvent externalEvent = event.getExternalEvent();
    Form<ExternalEvent> externalEventForm;

    if(externalEvent == null) {
      externalEventForm = Form.form(ExternalEvent.class);
    }
    else {
      externalEventForm = Form.form(ExternalEvent.class).fill(externalEvent);
    }

    return ok(views.html.privatemonitoring.alertdetails.render(event, externalEventForm));
  }

  @Security.Authenticated(Secured.class)
  public static Result updateEventDetails(Long eventId) {
    Alert event = Alert.find().where().eq("primaryKey", eventId).findUnique();
    Form<ExternalEvent> externalEventForm = Form.form(ExternalEvent.class).bindFromRequest();

    if (externalEventForm.hasErrors()) {
      return ok(error.render("Problem updating event", externalEventForm.errors().toString()));
    }

    ExternalEvent externalEvent = externalEventForm.get();
    externalEvent.getAlerts().add(event);
    event.setExternalEvent(externalEvent);
    externalEvent.save();
    event.save();

    flash("updated", "External Event Updated");

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
      return ok(error.render("Could not locate device with id", session("email")));
    }

    return redirect(routes.Events.nearbyEventsByPage(device.getDeviceId(), 0));
  }

  @Security.Authenticated(Secured.class)
  public static Result nearbyEventsByPage(Long deviceId, Integer page) {
    final int PAGE_SIZE = 10;
    OpqDevice device = OpqDevice.find().where()
                                .eq("deviceId", deviceId)
                                .findUnique();

    if(device == null) {
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

    List<Alert> events = Alert.find().where()
                              .startsWith("device.gridId", gridId.substring(0, gridId.length() - cnt))
                              .ne("device.deviceId", deviceId)
                              .eq("device.sharingData", true)
                              .order("timestamp desc")
                              .findPagingList(PAGE_SIZE)
                              .getPage(page).getList();

    return ok(views.html.privatemonitoring.nearbyevents.render(events, page, (events.size() / PAGE_SIZE), deviceId));
  }
}
