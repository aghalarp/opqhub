package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.adminalert;
import views.html.admin.admincdsi;
import views.html.admin.admindevice;
import views.html.admin.adminuser;

public class Administration extends Controller {

  public static Result user() {
    return ok(adminuser.render());
  }

  public static Result device() {
    return ok(admindevice.render());
  }

  public static Result alert() {
    return ok(adminalert.render());
  }

  public static Result cdsi() {
    return ok(admincdsi.render());
  }
}
