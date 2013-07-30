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
