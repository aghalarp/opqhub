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

/**
 * Contains methods which allow users to interact with the views and models for administrating their account and
 * their device.
 */
public class Administration extends Controller {

  /**
   * Render the view for user administration.
   * @return The rendered view for user administration.
   */
  public static Result user() {
    return ok(adminuser.render());
  }

  /**
   * Render the view for device administration.
   * @return The rendered view for device administration.
   */
  public static Result device() {
    return ok(admindevice.render());
  }

  /**
   * Render the view for alert administration.
   * @return The rendered view for alert administration.
   */
  public static Result alert() {
    return ok(adminalert.render());
  }

  /**
   * Render the view for CDSI administration.
   * @return The rendered view for CDSI administration.
   */
  public static Result cdsi() {
    return ok(admincdsi.render());
  }
}
