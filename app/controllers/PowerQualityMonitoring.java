/*
  This file is part of opq-ao.

  opa-ao is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opa-ao is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opq-ao.  If not, see <http://www.gnu.org/licenses/>.

  Copyright 2013 Anthony Christe
 */

package controllers;

import models.Alert;
import models.OpqDevice;
import models.Person;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods for modifying views and models for both private and public power quality monitoring.
 */
public class PowerQualityMonitoring extends Controller {

  /**
   * Render a public "google-maps" style map with devices and alerts from users who are participating in the CDSI.
   * @return Rendered view of publics devices and public alerts.
   */
  public static Result publicMonitor() {
    List<models.Alert> alerts = Alert.find().all();
    List<models.OpqDevice> devices = OpqDevice.find().where().eq("participatingInCdsi", true).findList();
    boolean loggedOut = !(session().containsKey("email"));
    return ok(views.html.publicpowerqualitymonitoring.render(alerts, devices, loggedOut));
  }

  /**
   * Render a view which contains a list of power quality events for the current logged in user.
   * @return Rendered view of power quality events for current user.
   */
  @Security.Authenticated(Secured.class)
  public static Result privateMonitor() {
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    List<Alert> alerts = new ArrayList<>();

    for(OpqDevice device : person.getDevices()) {
      for(Alert alert : device.getAlerts()) {
        alerts.add(alert);
      }
    }

    return ok(views.html.privatepowerqualitymonitoring.render(alerts));
  }

  /**
   * Mockup method that should be removed once the DB is correctly implemented.
   * @return A csv view of alerts.
   */
  public static Result getAlerts() {
    String alerts = "lat\tlon\ttitle\tdescription\ticon\n"
      + "21.3069\t-157.8583\tFrequency Alert\t57 Hz (4 sec)\t"
      + "http://localhost:9000/assets/images/frequency-alert-icon.png\n"
      + "21.4181\t-157.8036\tVoltage Alert\t115 V (3 sec)\t"
      + "http://localhost:9000/assets/images/voltage-alert-icon.png\n"
      + "21.3147\t-157.8081\tVoltage Alert\t122 V (4 sec)\t"
      + "http://localhost:9000/assets/images/voltage-alert-icon.png\n";
    return ok(alerts);
  }
}
