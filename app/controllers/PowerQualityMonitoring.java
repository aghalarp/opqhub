package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class PowerQualityMonitoring extends Controller {
  public static Result publicMonitor() {
    return ok(views.html.publicpowerqualitymonitoring.render());
  }

  public static Result privateMonitor() {
    return ok(views.html.privatepowerqualitymonitoring.render());
  }
}
