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

import models.Person;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.login;

import java.util.Arrays;

import static play.data.Form.form;

/**
 * Contains methods for interacting with the views and models for OPQ's homepage.
 */
public class Application extends Controller {

  /**
   * Render the view for the homepage.
   *
   * @return The rendered view for the homepage.
   */
  public static Result index() {
    return ok(index.render());
  }

  public static Result logout() {
    session().clear();
    flash("success", "You've been logged out");
    return redirect(routes.Application.login());
  }

  public static Result login() {
    return ok(login.render(form(Login.class)));
  }

  public static Result authenticate() {
    Form<Login> loginForm = form(Login.class).bindFromRequest();
    if (loginForm.hasErrors()) {
      return badRequest(login.render(loginForm));
    }
    else {
      session().clear();
      session("email", loginForm.get().email);
      return redirect(routes.PowerQualityMonitoring.privateMonitor());
    }
  }

  public static class Login {
    @Constraints.Required
    @Constraints.Email
    public String email;

    @Constraints.Required
    public String password;

    public String validate() {
      // First try to find a person with a matching email
      Person person = Person.find().where().eq("email", email).findUnique();
      if (person == null) {
        return "Invalid email or password";
      }
      // See if the passwords match
      byte[] hashedPassword = utils.FormUtils.hashPassword(password, person.getPasswordSalt());
      if (!Arrays.equals(person.getPasswordHash(), hashedPassword)) {
        return "Invalid email or password";
      }
      return null;
    }
  }
}
