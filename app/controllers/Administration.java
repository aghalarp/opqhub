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
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.adminalert;
import views.html.admin.admindatashare;
import views.html.admin.admindevice;
import views.html.admin.adminuser;
import views.html.admin.updatedatashare;
import views.html.error;

import java.util.ArrayList;
import java.util.List;

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
      Logger.debug(String.format("%s user information NOT updated due to errors %s", person.getPrimaryKey(), personForm.errors().toString()));
      return ok(error.render("Problem updating person", personForm.errors().toString()));
    }

    Logger.debug(String.format("%s user information updated", person.getPrimaryKey()));

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
      Logger.debug(String.format("New device not saved due to errors %s", opqDeviceForm.errors().toString()));
      return ok(error.render("Problem saving new device", opqDeviceForm.errors().toString()));
    }

    Person person = Person.find().where().eq("email", session("email")).findUnique();
    OpqDevice opqDevice = opqDeviceForm.get();
    person.getDevices().add(opqDevice);
    opqDevice.setPerson(person);
    opqDevice.save();
    person.save();

    Logger.debug(String.format("New device [%s] saved", opqDevice.getSharingData()));

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
      Logger.debug(String.format("device not updated due to %s", opqDeviceForm.errors().toString()));
      return ok(error.render("Problem updating device", opqDeviceForm.errors().toString()));
    }

    opqDeviceForm.get().update(opqDevice.getPrimaryKey());
    Logger.debug(String.format("device [%s] updated", opqDevice.getDeviceId()));
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

    Logger.debug(String.format("Device [%s] deleted", opqDevice.getDeviceId()));

    return redirect(routes.Administration.device());
  }

  /**
   * Render the view for alert administration.
   *
   * @return The rendered view for alert administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result alert() {
    /*Person person = Person.find().where().eq("email", session("email")).findUnique();
    List<OpqDevice> devices = person.getDevices();
    List<Form<Alert>> alertNotificationForms = new ArrayList<>();
    Form<Alert> alertNotificationForm;
    List<String> deviceIds = new ArrayList<>();

    // For each device, store the device id
    for (OpqDevice opqDevice : devices) {
      deviceIds.add(Long.toString(opqDevice.getDeviceId()));
      // For each alert notification per device, create a form with data from that alert notification.
      for (Alert alert : opqDevice.getAlerts()) {
        alertNotificationForm = form(Alert.class).fill(alert);
        alertNotificationForm.data().put("deviceId", Long.toString(opqDevice.getDeviceId()));
        alertNotificationForms.add(alertNotificationForm);
      }
    }

    alertNotificationForm = form(Alert.class);
    return ok(adminalert.render(alertNotificationForm, alertNotificationForms, deviceIds));*/
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    List<OpqDevice> devices = person.getDevices();

    return ok(adminalert.render(devices));
  }

  /**
   * Saves a bew alert notification to the DB.
   * @return Redirect to alert administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result saveAlert() {
    Form<Alert> alertNotificationForm = form(Alert.class).bindFromRequest();

    if (alertNotificationForm.hasErrors()) {
      Logger.debug(String.format("Could not save alert due to %s", alertNotificationForm.errors().toString()));
      return ok(error.render("Problem creating new alert notification", alertNotificationForm.errors().toString()));
    }

    Alert alert = alertNotificationForm.get();
    OpqDevice opqDevice =
        OpqDevice.find().where().eq("deviceId", alertNotificationForm.data().get("deviceId")).findUnique();

    opqDevice.getAlerts().add(alert);
    alert.setDevice(opqDevice);

    opqDevice.save();
    alert.save();
    flash("added", "Added new device alert");
    Logger.debug(String.format("Added device [%s] alert", opqDevice.getDeviceId()));
    return redirect(routes.Administration.alert());
  }

  /**
   * Updates an alert notification.
   * @param id The primary key of the alert notifiation.
   * @return Redirect to alert administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result updateAlert(String id) {
    Form<Alert> alertNotificationForm = form(Alert.class).bindFromRequest();

    if (alertNotificationForm.hasErrors()) {
      Logger.debug(String.format("Could not update alert notification %s", alertNotificationForm.errors().toString()));
      return ok(error.render("Problem updating alert notifications", alertNotificationForm.errors().toString()));
    }

    alertNotificationForm.get().update(Long.parseLong(id));
    flash("updated", "Updated device alert");
    Logger.debug(String.format("Alert notification updated"));
    return redirect(routes.Administration.alert());
  }

  /**
   * Render the view for CDSI administration.
   *
   * @return The rendered view for CDSI administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result dataSharing() {
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    List<OpqDevice> opqDevices = person.getDevices();

    return ok(admindatashare.render(opqDevices));
  }

  /**
   * Update CDSI information connected to an opq device.
   * @param deviceId The device id.
   * @return Redirect to cdsi administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result editDataSharing(Long deviceId) {
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
      Logger.error(String.format("Unknown device [%s] when trying to edit data sharing", session("email")));
      return ok(error.render("Unknown device", ""));
    }

    opqDeviceForm = form(OpqDevice.class).fill(opqDevice);
    Logger.debug(String.format("Data sharing updated for [%s]", opqDevice.getDeviceId()));
    return ok(updatedatashare.render(opqDeviceForm));
  }

  @Security.Authenticated(Secured.class)
  public static Result updateDataSharing(Long primaryKey) {
    Form<OpqDevice> opqDeviceForm = form(OpqDevice.class).bindFromRequest();

    if(opqDeviceForm.hasErrors()) {
      Logger.debug(String.format("Could not update data sharing due to %s", opqDeviceForm.errors().toString()));
      return ok(error.render("Problem updating data share", opqDeviceForm.errors().toString()));
    }

    opqDeviceForm.get().update(primaryKey);
    flash("updated", "Data sharing updated");
    Logger.debug(String.format("Data sharing updated for [%s]", opqDeviceForm.data().get("email")));
    return redirect(routes.Administration.dataSharing());
  }
}
