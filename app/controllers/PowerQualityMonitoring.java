package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class PowerQualityMonitoring extends Controller {
  public static Result index() {
    return ok(views.html.powerqualitymonitoring.render());
  }

}
