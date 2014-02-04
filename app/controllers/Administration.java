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

import models.AlertNotification;
import models.OpqDevice;
import models.Person;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.adminalert;
import views.html.admin.admincdsi;
import views.html.admin.admindevice;
import views.html.admin.adminuser;
import views.html.admin.updatecdsi;
import views.html.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static play.data.Form.form;

/**
 * Contains methods which allow users to interact with the views and models for administrating their account and their
 * device.
 */
public class Administration extends Controller {

  /**
   * Render the view for user administration.
   *
   * @return The rendered view for user administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result user() {
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    Form<Person> personForm = form(Person.class).fill(person);
    return ok(adminuser.render(personForm));
  }

  /**
   * Updates person fields and redirects back to person administration.
   * @return Redirect to person administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result updateUser() {
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    Form<Person> personForm = form(Person.class).bindFromRequest();

    if (personForm.hasErrors()) {
      return ok(error.render("Problem updating person", personForm.errors().toString()));
    }

    personForm.get().update(person.getPrimaryKey());
    return redirect(routes.Administration.user());
  }

  /**
   * Render the view for device administration.
   *
   * @return The rendered view for device administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result device() {
    Form<OpqDevice> opqDeviceForm = form(OpqDevice.class);
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    List<OpqDevice> opqDevices = person.getDevices();
    List<Form<OpqDevice>> opqDeviceForms = new ArrayList<>();

    // For each device, fill a form with values from that device
    for (OpqDevice opqDevice : opqDevices) {
      opqDeviceForms.add(form(OpqDevice.class).fill(opqDevice));
    }

    return ok(admindevice.render(opqDeviceForm, opqDeviceForms));
  }

  /**
   * Saves a new OPQ device to the DB.
   * @return Redirect to device administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result saveDevice() {
    Form<OpqDevice> opqDeviceForm = form(OpqDevice.class).bindFromRequest();

    if (opqDeviceForm.hasErrors()) {
      return ok(error.render("Problem saving new device", opqDeviceForm.errors().toString()));
    }

    Person person = Person.find().where().eq("email", session("email")).findUnique();
    OpqDevice opqDevice = opqDeviceForm.get();
    person.getDevices().add(opqDevice);
    opqDevice.setPerson(person);
    opqDevice.save();
    person.save();

    flash("added", "Device added");
    return redirect(routes.Administration.device());
  }

  /**
   * Updates device fields.
   * @param deviceId The device id.
   * @return Redirect to device administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result updateDevice(Long deviceId) {
    OpqDevice opqDevice = OpqDevice.find().where().eq("deviceId", deviceId).findUnique();
    Form<OpqDevice> opqDeviceForm = form(OpqDevice.class).bindFromRequest();

    if (opqDeviceForm.hasErrors()) {
      return ok(error.render("Problem updating device", opqDeviceForm.errors().toString()));
    }

    opqDeviceForm.get().update(opqDevice.getPrimaryKey());
    flash("updated", "Device updated");
    return redirect(routes.Administration.device());
  }


  /**
   * Warning: Don't call this unless you want every relationship attached to it deleted as well.
   *
   * @param deviceId Device id.
   *
   * @return Redirect back to devices page.
   */
  @Security.Authenticated(Secured.class)
  public static Result deleteDevice(Long deviceId) {
    OpqDevice opqDevice = OpqDevice.find().where().eq("deviceId", deviceId).findUnique();
    opqDevice.delete();
    opqDevice.save();

    return redirect(routes.Administration.device());
  }

  /**
   * Render the view for alert administration.
   *
   * @return The rendered view for alert administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result alert() {
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    List<OpqDevice> devices = person.getDevices();
    List<Form<AlertNotification>> alertNotificationForms = new ArrayList<>();
    Form<AlertNotification> alertNotificationForm;
    List<String> deviceIds = new ArrayList<>();

    // For each device, store the device id
    for (OpqDevice opqDevice : devices) {
      deviceIds.add(Long.toString(opqDevice.getDeviceId()));
      // For each alert notification per device, create a form with data from that alert notification.
      for (AlertNotification alertNotification : opqDevice.getAlertNotifications()) {
        alertNotificationForm = form(AlertNotification.class).fill(alertNotification);
        alertNotificationForm.data().put("deviceId", Long.toString(opqDevice.getDeviceId()));
        alertNotificationForms.add(alertNotificationForm);
      }
    }

    alertNotificationForm = form(AlertNotification.class);
    return ok(adminalert.render(alertNotificationForm, alertNotificationForms, deviceIds));
  }

  /**
   * Saves a bew alert notification to the DB.
   * @return Redirect to alert administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result saveAlert() {
    Form<AlertNotification> alertNotificationForm = form(AlertNotification.class).bindFromRequest();

    if (alertNotificationForm.hasErrors()) {
      return ok(error.render("Problem creating new alert notification", alertNotificationForm.errors().toString()));
    }

    AlertNotification alertNotification = alertNotificationForm.get();
    OpqDevice opqDevice =
        OpqDevice.find().where().eq("deviceId", alertNotificationForm.data().get("deviceId")).findUnique();

    opqDevice.getAlertNotifications().add(alertNotification);
    alertNotification.setDevice(opqDevice);

    opqDevice.save();
    alertNotification.save();
    flash("added", "Added new device alert");
    return redirect(routes.Administration.alert());
  }

  /**
   * Updates an alert notification.
   * @param id The primary key of the alert notifiation.
   * @return Redirect to alert administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result updateAlert(String id) {
    Form<AlertNotification> alertNotificationForm = form(AlertNotification.class).bindFromRequest();

    if (alertNotificationForm.hasErrors()) {
      return ok(error.render("Problem updating alert notifications", alertNotificationForm.errors().toString()));
    }

    alertNotificationForm.get().update(Long.parseLong(id));
    flash("updated", "Updated device alert");
    return redirect(routes.Administration.alert());
  }

  /**
   * Render the view for CDSI administration.
   *
   * @return The rendered view for CDSI administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result cdsi() {
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    List<OpqDevice> opqDevices = person.getDevices();

    return ok(admincdsi.render(opqDevices));
  }

  /**
   * Update CDSI information connected to an opq device.
   * @param deviceId The device id.
   * @return Redirect to cdsi administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result editCdsi(Long deviceId) {
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    List<OpqDevice> opqDevices = person.getDevices();
    OpqDevice opqDevice = null;
    Form<OpqDevice> opqDeviceForm;

    for(OpqDevice device : opqDevices) {
      if(device.getDeviceId().equals(deviceId)) {
        opqDevice = device;
        break;
      }
    }

    if(opqDevice == null) {
      return ok(error.render("Unknown device", ""));
    }

    opqDeviceForm = form(OpqDevice.class).fill(opqDevice);

    return ok(updatecdsi.render(opqDeviceForm));
  }

  @Security.Authenticated(Secured.class)
  public static Result updateCdsi(Long primaryKey) {
    Form<OpqDevice> opqDeviceForm = form(OpqDevice.class).bindFromRequest();

    if(opqDeviceForm.hasErrors()) {
      return ok(error.render("Problem updating CDSI", opqDeviceForm.errors().toString()));
    }

    opqDeviceForm.get().update(primaryKey);
    flash("updated", "CDSI updated");
    return redirect(routes.Administration.cdsi());
  }



  private static String getGeocodedString(Map<String, String> data) {
    StringBuilder stringBuilder = new StringBuilder();

    String state = (data.containsKey("state")) ? data.get("state") : "";
    String city = (data.containsKey("city")) ? data.get("city") : "";
    String postalCode = (data.containsKey("zip")) ? data.get("zip") : "";
    String streetNumber = (data.containsKey("streetNumber")) ? data.get("streetNumber") : "";
    String streetName = (data.containsKey("streetName")) ? data.get("streetName") : "";

    String searchUrl = "http://nominatim.openstreetmap.org/search";
    String queryString = String.format("?state=%s&city=%s&postalcode=%s&street=%s+%s&format=json&email=achriste@hawaii.edu",
                                       state, city, postalCode, streetNumber, streetName);

    return String.format("%s%s", searchUrl, queryString);
  }
}
