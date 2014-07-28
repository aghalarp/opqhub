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

import models.OpqDevice;
import models.Person;
import play.Logger;
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
   *
   * @return Rendered view of the sign-up wizard.
   */
  public static Result index() {
    Form<Person> personForm = form(Person.class);
    Form<OpqDevice> opqDeviceForm = form(OpqDevice.class);

    return ok(wizard.render(personForm, opqDeviceForm));
  }

  /**
   * Persists information from wizard view to DB.
   *
   * @return A redirection back to the home page.
   */
  public static Result save() {
    // Get person information
    Form<Person> personForm = form(Person.class).bindFromRequest();
    if (personForm.hasErrors()) {
      Logger.debug(String.format("Wizard person form errors %s", personForm.errors().toString()));
      return makeError("Person form validation errors", personForm.errors());
    }
    Person person = personForm.get();

    // Get device information
    Form<OpqDevice> opqDeviceForm = form(OpqDevice.class).bindFromRequest();
    if (opqDeviceForm.hasErrors()) {
      Logger.debug(String.format("Wizard device form errors %s", opqDeviceForm.errors().toString()));
      return makeError("Error parsing OPQ Device info", opqDeviceForm.errors());
    }
    OpqDevice opqDevice = opqDeviceForm.get();
    //TODO: Save with key
    // Now that we have all the data, it should be possible to complete all the relationships.
    //person.getDevices().add(opqDevice);
   //opqDevice.setPerson(person);


    // Persist everything to the DB
    person.save();
    opqDevice.save();

    Logger.info(String.format("Successful creation of device [%s] and user [%s] through wizard",                              opqDevice.getDeviceId(), person.getEmail()));
    return redirect(routes.Application.index());
  }

  /**
   * Makes an error message out of a short description and a list of errors.
   *
   * This method should eventually be deleted as full validation is used.
   * @param name Name of the error (short description).
   * @param errors Map of errors returned by play.
   * @return Rendered error page displaying errors.
   */
  private static Result makeError(String name, Map<String, List<ValidationError>> errors) {
    return ok(error.render(name, errors.toString()));
  }
}
