package controllers;

import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.error;
import views.html.signup;

import java.util.List;
import java.util.Map;

public class Person extends Controller {
  public static Result signup() {
    Form<models.Person> form = Form.form(models.Person.class);
    return ok(signup.render(form));
  }

  public static Result save() {
    Form<models.Person> form = Form.form(models.Person.class).bindFromRequest();

    // Validate form
    if (form.hasErrors()) {
      Logger.debug(String.format("Wizard person form errors %s", form.errors().toString()));
      return makeError(form.errors());
    }

    // Save account information
    models.Person person = form.get();
    person.save();

    // Log the person in
    session("email", person.getEmail());

    return redirect(controllers.routes.Administration.device());
  }

  /**
   * Makes an error message out of a short description and a list of errors.
   *
   * This method should eventually be deleted as full validation is used.
   * @param errors Map of errors returned by play.
   * @return Rendered error page displaying errors.
   */
  private static Result makeError(Map<String, List<ValidationError>> errors) {
    return ok(error.render("Can not create new account due to the following errors", errors.toString()));
  }
}
