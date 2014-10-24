package controllers;

import controllers.SecuredAndMatched;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class PrivatePowerQuality extends Controller {
    @Security.Authenticated(SecuredAndMatched.class)
    public static Result getTrends(String email) {
        return ok(views.html.privatetrends.render());
    }

  @Security.Authenticated(SecuredAndMatched.class)
  public static Result getMain(String email) {
    return ok(views.html.privatemap.render());
  }
}


