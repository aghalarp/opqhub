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

  Copyright 2013 Anthony Christe
 */

package controllers;

import models.Person;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.login;
import views.html.signup;

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
    return ok(index.render(!session().containsKey("email")));
  }

  public static Result signup() {
    Form<Person> form = Form.form(Person.class);
    return ok(signup.render(form));
  }

  /**
   * Logs out current user by clearing the session.
   * @return Redirect to log-in page.
   */
  public static Result logout() {
    session().clear();
    flash("success", "You've been logged out");
    return redirect(routes.Application.login());
  }

  /**
   * Display the login page.
   * @return Rendered view of the login page.
   */
  public static Result login() {
    return ok(login.render(form(Login.class)));
  }

  /**
   * Authenticates a user by storing their email in the session.
   * @return Redirect to private power monitoring.
   */
  public static Result authenticate() {
    Form<Login> loginForm = form(Login.class).bindFromRequest();
    if (loginForm.hasErrors()) {
      Logger.info(String.format("Bad login attempt from %s", loginForm.data().get("email")));
      return badRequest(login.render(loginForm));
    }
    else {
      session().clear();
      session("email", loginForm.get().email);
      return redirect(routes.Events.eventsByPage(0, 0L));
    }
  }

  /**
   * This class is used as an object to bind to the login form.
   */
  public static class Login {
    /**
     * E-mail address of the user.
     */
    @Constraints.Required
    @Constraints.Email
    public String email;

    /**
     * Password of the user.
     */
    @Constraints.Required
    public String password;

    /**
     * Attempts to validate user by first matching the e-mail, and then matching the password hash.
     * @return Either an error message or null for success.
     */
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
