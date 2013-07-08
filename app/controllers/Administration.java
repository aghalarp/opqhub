package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Administration extends Controller {

  public static Result user() {
    return ok(views.html.adminuser.render());
  }

  public static Result device() {
    return ok(views.html.admindevice.render());
  }

  public static Result alert() {
    return ok(views.html.adminalert.render());
  }

  public static Result cdsi() {
    return ok(views.html.admincdsi.render());
  }
}
