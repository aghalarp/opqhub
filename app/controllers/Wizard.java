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
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.error;
import views.html.wizard.wizard;

import java.util.List;
import java.util.Map;

import static play.data.Form.form;

/**
 * Contains methods for interacting with views and models for sign-up wizard.
 */
public class Wizard extends Controller {
  /**
   * Render the view for the sign-up wizard.
   * @return Rendered view of the sign-up wizard.
   */
  public static Result index() {
    Form<Person> personForm = form(Person.class);
    Form<OpqDevice> opqDeviceForm = form(OpqDevice.class);
    Form<AlertNotification> alertNotificationForm = form(AlertNotification.class);

    return ok(wizard.render(personForm, opqDeviceForm, alertNotificationForm));
  }

  /**
   * Persists information from wizard view to DB.
   * @return A redirection back to the home page.
   */
  public static Result save() {
    // Get person information
    Form<Person> personForm = form(Person.class).bindFromRequest();
    if(personForm.hasErrors()) {
      return makeError("Person form validation errors", personForm.errors());
    }
    Person person = personForm.get();

    // Get device information
    Form<OpqDevice> opqDeviceForm = form(OpqDevice.class).bindFromRequest();
    if(opqDeviceForm.hasErrors()) {
      return makeError("Error parsing OPQ Device info", opqDeviceForm.errors());
    }
    OpqDevice opqDevice = opqDeviceForm.get();

    // Get alert notification information
    Form<AlertNotification> alertNotificationForm = form(AlertNotification.class).bindFromRequest();
    if(alertNotificationForm.hasErrors()) {
      return makeError("Error parsing alert info", alertNotificationForm.errors());
    }
    AlertNotification alertNotification = alertNotificationForm.get();

    // Now that we have all the data, it should be possible to complete all the relationships.
    person.getDevices().add(opqDevice);
    opqDevice.setPerson(person);
    alertNotification.setDevice(opqDevice);
    opqDevice.getAlertNotifications().add(alertNotification);

    // Persist everything to the DB
    person.save();
    opqDevice.save();
    alertNotification.save();

    return redirect(routes.Application.index());
  }

  private static Result makeError(String name, Map<String, List<ValidationError>> errors) {
    return ok(error.render(name, errors.toString()));
  }
}
