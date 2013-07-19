package controllers;

import models.Alert;
import models.OpqDevice;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PowerQualityMonitoring extends Controller {
  public static Result publicMonitor() {
    List<Alert> alerts = new LinkedList<>();
    List<OpqDevice> devices = new LinkedList<OpqDevice>();

    // For mockup purposes, lets create some fake devices, and fake alerts.
    devices.add(new OpqDevice(0L, 21.3069, -157.8583)); // Honolulu
    devices.add(new OpqDevice(1L, 21.4022, -157.7394)); // Kailua
    devices.add(new OpqDevice(2L, 21.3147, -157.8081)); // Manoa
    devices.add(new OpqDevice(3L, 21.4447, -158.1900)); // Waianae
    devices.add(new OpqDevice(4L, 21.6536, -157.9272)); // Laie
    devices.add(new OpqDevice(5L, 21.4181, -157.8036)); // Kaneohe
    devices.add(new OpqDevice(6L, 21.3147, -157.8081)); // Manoa
    devices.add(new OpqDevice(7L, 21.3619, -157.9536)); // Pearl Harbor
    devices.add(new OpqDevice(8L, 21.2695, -157.8196)); // Waikiki
    devices.add(new OpqDevice(9L, 21.4511, -158.0156)); // Mililani

    alerts.add(new Alert(Alert.AlertType.FREQUENCY, new Date(), 1000L, 57.8, "Low Frequency (-1.2 Hz)", 21.3069, -157.8583)); // Honolulu
    alerts.add(new Alert(Alert.AlertType.VOLTAGE, new Date(), 2500L, 122.0, "High Voltage (+2 V)", 21.4022, -157.7394)); // Kailua
    alerts.add(new Alert(Alert.AlertType.VOLTAGE, new Date(), 500L, 118.3, "Low Voltage (-1.7 V)", 21.3147, -157.8081)); // Manoa

    return ok(views.html.publicpowerqualitymonitoring.render(alerts, devices));
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
