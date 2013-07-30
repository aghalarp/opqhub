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

import models.Alert;
import models.OpqDevice;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PowerQualityMonitoring extends Controller {
  public static Result publicMonitor() {
    List<models.Alert> alerts = new LinkedList<>();
    List<models.OpqDevice> devices = OpqDevice.find().where().eq("participatingInCdsi", true).findList();


    return ok(views.html.publicpowerqualitymonitoring.render(alerts, devices, true));
  }

  public static Result privateMonitor() {
    return ok(views.html.privatepowerqualitymonitoring.render());
  }

  public static Result getAlerts() {
    String alerts = "lat\tlon\ttitle\tdescription\ticon\n"
      + "21.3069\t-157.8583\tFrequency Alert\t57 Hz (4 sec)\t"
      + "http://localhost:9000/assets/images/frequency-alert-icon.png\n"
      + "21.4181\t-157.8036\tVoltage Alert\t115 V (3 sec)\t"
      + "http://localhost:9000/assets/images/voltage-alert-icon.png\n"
      + "21.3147\t-157.8081\tVoltage Alert\t122 V (4 sec)\t"
      + "http://localhost:9000/assets/images/voltage-alert-icon.png\n";
    return ok(alerts);
  }
}
