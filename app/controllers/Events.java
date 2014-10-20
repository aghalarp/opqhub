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
  public static Result eventsByPage(Integer page, Long afterTimestamp) {
    return TODO;
  }
}
