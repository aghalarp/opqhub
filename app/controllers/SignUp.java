package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.signup;

public class SignUp extends Controller {

     public static Result signUp() {
       return ok(signup.render());
     }
}
