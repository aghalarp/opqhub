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
}
