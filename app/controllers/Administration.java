/*
  This file is part of OPQHub.

  OPQHub is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  OPQHub is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with OPQHub.  If not, see <http://www.gnu.org/licenses/>.

  Copyright 2014 Anthony Christe
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
import views.html.admin.admindevice;
import views.html.admin.adminuser;
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
    Person person = Person.getLoggedIn();
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
    AccessKey existingKey;
    Person person = Person.getLoggedIn();

    if(AccessKey.keyExists(key)) {
      existingKey = AccessKey.findKey(key);
      existingKey.getPersons().add(person);
      existingKey.update();
    }
    else {
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
    }

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
}
