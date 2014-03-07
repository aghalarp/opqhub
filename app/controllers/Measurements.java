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

import models.Measurement;
import models.OpqDevice;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.DateUtils;
import views.html.privatemonitoring.privatemeasurements;

import java.util.List;

/**
 * Contains methods for modifying views and models for both private and public power quality monitoring.
 */
public class Measurements extends Controller {
  @Security.Authenticated(Secured.class)
  public static Result measurements() {
    // Get the first available device
    OpqDevice device = OpqDevice.find().where().eq("person.email", session("email")).findList().get(0);

    // TODO: Investigate what happens when a device is not returned

    return redirect(routes.Measurements.measurementsByPage(device.getDeviceId(), 0, 0L));
  }

  @Security.Authenticated(Secured.class)
  public static Result filterMeasurements() {
    DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
    Long deviceId = Long.parseLong(dynamicForm.get("deviceId"));
    String selectedTimeUnit = dynamicForm.get("pastTimeSelect");

    Long adjustedTimestamp = DateUtils.getMillis() - DateUtils.TimeUnit.valueOf(selectedTimeUnit).getMilliseconds();
    session("pastTimeSelectMeasurements", selectedTimeUnit);
    session("measurementsAfterAmount", adjustedTimestamp.toString());
    return redirect(routes.Measurements.measurementsByPage(deviceId, 0, adjustedTimestamp));
  }

  @Security.Authenticated(Secured.class)
  public static Result measurementsByPage(Long deviceId, Integer page, Long afterTimestamp) {
    Integer pages;
    final Integer ROWS_PER_PAGE = 10;
    Long after = (afterTimestamp == null) ? 0 : afterTimestamp;
    List<Measurement> measurements = Measurement.find().where()
                                                .eq("device.deviceId", deviceId)
                                                .gt("timestamp", after)
                                                .order("timestamp desc")
                                                .findPagingList(ROWS_PER_PAGE)
                                                .getPage(page)
                                                .getList();

    pages = Measurement.find().where().eq("device.deviceId", deviceId).gt("timestamp", after).findRowCount() / ROWS_PER_PAGE;

    return ok(privatemeasurements.render(measurements, deviceId, page, pages));
  }
}
