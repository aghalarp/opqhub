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
import models.ExternalEvent;
import models.Measurement;
import models.OpqDevice;
import models.Person;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.TimestampComparator;
import views.html.error;
import views.html.privatemonitoring.privatemeasurements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains methods for modifying views and models for both private and public power quality monitoring.
 */
public class PowerQualityMonitoring extends Controller {

  /**
   * Render a public "google-maps" style map with devices and alerts from users who are participating in the CDSI.
   *
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
   *
   * @return Rendered view of power quality events for current user.
   */
  @Security.Authenticated(Secured.class)
  public static Result privateAlertsMonitor() {
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    List<Alert> alerts = new ArrayList<>();

    // Search for all devices connected to current user, then for each device find its alerts
    for (OpqDevice device : person.getDevices()) {
      for (Alert alert : device.getAlerts()) {
        alerts.add(alert);
      }
    }

    Collections.sort(alerts, new TimestampComparator());
    return ok(views.html.privatemonitoring.privatealerts.render(alerts));
  }

  @Security.Authenticated(Secured.class)
  public static Result alertDetails(Long alertId) {
    Alert alert = Alert.find().where().eq("primaryKey", alertId).findUnique();
    ExternalEvent externalEvent = alert.getExternalEvent();
    Form<ExternalEvent> externalEventForm;

    if(externalEvent == null) {
      externalEventForm = Form.form(ExternalEvent.class);
    }
    else {
      externalEventForm = Form.form(ExternalEvent.class).fill(externalEvent);
    }

    return ok(views.html.privatemonitoring.alertdetails.render(alert, externalEventForm));
  }

  @Security.Authenticated(Secured.class)
  public static Result updateAlertDetails(Long alertId) {
    Alert alert = Alert.find().where().eq("primaryKey", alertId).findUnique();
    Form<ExternalEvent> externalEventForm = Form.form(ExternalEvent.class).bindFromRequest();

    if (externalEventForm.hasErrors()) {
      return ok(error.render("Problem updating alert", externalEventForm.errors().toString()));
    }

    ExternalEvent externalEvent = externalEventForm.get();
    externalEvent.getAlerts().add(alert);
    alert.setExternalEvent(externalEvent);
    externalEvent.save();
    alert.save();

    flash("updated", "External Event Updated");

    return redirect(routes.PowerQualityMonitoring.alertDetails(alertId));
  }

  @Security.Authenticated(Secured.class)
  public static Result privateMeasurementsMonitor() {
    // Get the first available device
    OpqDevice device = OpqDevice.find().where().eq("person.email", session("email")).findList().get(0);

    // TODO: Investigate what happens when a device is not returned

    return redirect(routes.PowerQualityMonitoring.privateMeasurementsMonitorByPage(device.getDeviceId(), 0));
  }

  @Security.Authenticated(Secured.class)
  public static Result privateMeasurementsMonitorByPage(Long deviceId, Integer p) {
    OpqDevice device = OpqDevice.find().byId(deviceId);
    List<Measurement> measurements = Measurement.find().where()
        .eq("device.deviceId", deviceId)
        .order("timestamp desc")
        .findPagingList(10)
        .getPage(p)
        .getList();

    return ok(privatemeasurements.render(measurements, deviceId, p));
  }
}
