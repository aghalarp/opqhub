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

  public static Result getAlerts() {
    String alerts = "lat\tlon\ttitle\tdescription\ticon\n" +
      "21.3069\t-157.8583\tFrequency Alert\t57 Hz (4 sec)\thttp://localhost:9000/assets/images/frequency-alert-icon.png\n" +
      "21.4181\t-157.8036\tVoltage Alert\t115 V (3 sec)\thttp://localhost:9000/assets/images/voltage-alert-icon.png\n" +
      "21.3147\t-157.8081\tVoltage Alert\t122 V (4 sec)\thttp://localhost:9000/assets/images/voltage-alert-icon.png\n";
    return ok(alerts);
  }
}
