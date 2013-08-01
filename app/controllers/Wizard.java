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
import views.html.wizard.wizard;

/**
 * Contains methods for interacting with views and models for sign-up wizard.
 */
public class Wizard extends Controller {
  /**
   * Render the view for the sign-up wizard.
   * @return Rendered view of the sign-up wizard.
   */
  public static Result index() {
    return ok(wizard.render());
  }
}
