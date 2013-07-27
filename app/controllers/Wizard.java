package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.wizard.wizard;

public class Wizard extends Controller {
  public static Result index() {
    return ok(wizard.render());
  }
}
