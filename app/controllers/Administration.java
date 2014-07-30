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
import models.Location;
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

    Person person = Person.find().where().eq("email", session("email")).findUnique();
    Set<AccessKey> keys = person.getAccessKeys();

    return ok(admindevice.render(keyForm, keys));
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
    OpqDevice device = new OpqDevice(key.getDeviceId());

    key.save();
    device.save();

    // Update person
    person.getAccessKeys().add(key);
    person.update();

    // Update key
    key.getPersons().add(person);
    key.setOpqDevice(device);
    key.update();

    // Update device
    device.setAccessKey(key);
    device.update();


//    // If this key already exists, we want to use that key
//    if(AccessKey.keyExists(key)) {
//      key = AccessKey.findKey(key);
//      person.getAccessKeys().add(key);
//      person.update();
//      key.getPersons().add(person);
//      key.update();
//    }
//    else {
//      OpqDevice device = new OpqDevice(key.getDeviceId());
//      device.save();
//      key.save();
//
//      key.getPersons().add(person);
//      key.setOpqDevice(device);
//      key.update();
//
//      device.setAccessKey(key);
//      device.update();
//
//      person.getAccessKeys().add(key);
//      person.update(person.getPrimaryKey());
//
//      /*
//      OpqDevice device = new OpqDevice(key.getDeviceId());
//      key.getPersons().add(person);
//      key.setOpqDevice(device);
//      key.save();
//      device.setAccessKey(key);
//      device.save();
//      person.getAccessKeys().add(key);
//      person.update(person.getPrimaryKey());
//      */
//    }

    Logger.debug(String.format("New key [%s] saved", key));

    flash("added", "Device added");
    return redirect(routes.Administration.configureDevice(key.getDeviceId(), key.getAccessKey()));
  }

  public static Result configureDevice(Long deviceId, String accessKey) {
    AccessKey key = AccessKey.findKey(deviceId, accessKey);
    OpqDevice device = key.getOpqDevice();
    Location location = device.getLocation();
    Form<OpqDevice> deviceForm  = form(OpqDevice.class).fill(device);
    Form<Location> locationForm = (location == null) ? form(Location.class) : form(Location.class).fill(location);
    return ok(views.html.admin.deviceconfig.render(key.getDeviceId(), key.getAccessKey(), deviceForm, locationForm, location));
  }

  public static Result saveDeviceConfiguration() {
    Form<OpqDevice> deviceForm = form(OpqDevice.class).bindFromRequest();
    Form<Location> locationForm = form(Location.class).bindFromRequest();

    if (deviceForm.hasErrors()) {
      Logger.debug(String.format("Device not updated due to errors %s", deviceForm.errors().toString()));
      return ok(error.render("Problem updating device", deviceForm.errors().toString()));
    }
    if (locationForm.hasErrors()) {
      Logger.debug(String.format("Location not updated due to errors %s", locationForm.errors().toString()));
      return ok(error.render("Problem updating location...", locationForm.errors().toString()));
    }

    OpqDevice deviceFromForm = deviceForm.get();
    Location location = locationForm.get();

    OpqDevice device = OpqDevice.find().where().eq("deviceId", deviceFromForm.getDeviceId()).findUnique();

    device.setDescription(deviceFromForm.getDescription());
    device.setSharingData(deviceFromForm.getSharingData());
    device.setLocation(location);
    device.update();

    Location tmp = Location.find().where().eq("gridId", location.getGridId()).findUnique();

    Long locationPk;

    location.getOpqDevices().add(device);

    if(tmp == null) {
      location.save();
    }
    else {
      locationPk = tmp.getPrimaryKey();
      location.update(locationPk);
    }

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
