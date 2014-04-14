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

import models.Event;

import models.OpqDevice;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * Contains methods for modifying views and models for both private and public power quality monitoring.
 */
public class PowerQualityMonitoring extends Controller {

  /**
   * Render a public "google-maps" style map with devices and alerts from users who are participating in data sharing.
   *
   * @return Rendered view of publics' devices and public alerts.
   */
  public static Result publicMonitor() {
    List<Event> events = Event.find().all();
    List<models.OpqDevice> devices = OpqDevice.find().where().eq("sharingData", true).findList();
    boolean loggedOut = !(session().containsKey("email"));
    return ok(views.html.publicpowerqualitymonitoring.render(events, devices, loggedOut));
  }

}
