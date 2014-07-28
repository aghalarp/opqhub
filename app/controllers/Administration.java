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

import models.AccessKey;
import models.OpqDevice;
import models.Person;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.admindatashare;
import views.html.admin.admindevice;
import views.html.admin.adminuser;
import views.html.admin.updatedatashare;
import views.html.error;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    return redirect(controllers.routes.Administration.user());
  }

  /**
   * Render the view for device administration.
   *
   * @return The rendered view for device administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result device() {
    Form<AccessKey> keyForm = form(AccessKey.class);
    List<Form<AccessKey>> keyForms = new ArrayList<>();

    Person person = Person.find().where().eq("email", session("email")).findUnique();
    Set<AccessKey> keys = person.getAccessKeys();

    // For each device, fill a form with values from that device
    for (AccessKey key : keys) {
      keyForms.add(form(AccessKey.class).fill(key));
    }

    return ok(admindevice.render(keyForm, keyForms));
  }

  /**
   * Saves a new OPQ device to the DB.
   * @return Redirect to device administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result saveDevice() {
    Form<AccessKey> keyForm = form(AccessKey.class).bindFromRequest();

    if (keyForm.hasErrors()) {
      Logger.debug(String.format("New device not saved due to errors %s", keyForm.errors().toString()));
      return ok(error.render("Problem saving new device", keyForm.errors().toString()));
    }

    AccessKey key = keyForm.get();
    Person person = Person.find().where().eq("email", session("email")).findUnique();

    // If this key already exists, we want to use that key
    if(AccessKey.keyExists(key)) {
      key = AccessKey.findKey(key);
    }

    person.getAccessKeys().add(key);
    person.update();

    key.getPersons().add(person);

    if(AccessKey.keyExists(key)) {
      key.update();
    }
    else {
      key.save();
    }

    Logger.debug(String.format("New key [%s] saved", key));

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
   * Render the view for data sharing administration.
   *
   * @return The rendered view for data sharing administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result dataSharing() {
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    Set<AccessKey> accessKeys = person.getAccessKeys();
    List<OpqDevice> opqDevices = new LinkedList<>();
    for(AccessKey accessKey : accessKeys) {
      opqDevices.add(accessKey.getOpqDevice());
    }

    return ok(admindatashare.render(opqDevices));
  }

  /**
   * Update data sharing information connected to an opq device.
   * @param deviceId The device id.
   * @return Redirect to data sharing administration.
   */
  @Security.Authenticated(Secured.class)
  public static Result editDataSharing(Long deviceId) {
    Person person = Person.find().where().eq("email", session("email")).findUnique();
    Set<AccessKey> accessKeys = person.getAccessKeys();
    List<OpqDevice> opqDevices = new LinkedList<>();
    for(AccessKey accessKey : accessKeys) {
      opqDevices.add(accessKey.getOpqDevice());
    }
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
