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

package tests;

import models.*;
import org.junit.*;
import play.test.*;

import java.util.*;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

/**
 * Contains methods that test the model portion of our MVC.
 */
public class ModelTests {
  private FakeApplication fakeApplication;

  /**
   * Before the test has begun, start a new fake application.
   */
  @Before
  public void startApp() {
    fakeApplication = fakeApplication(inMemoryDatabase());
    start(fakeApplication);
  }

  /**
   * Once the test has finished, stop the fake application.
   */
  @After
  public void stopApp() {
    stop(fakeApplication);
  }

  /**
   * Tests that models can be saved and retrieved from the DB.
   * Also tests that the relationships between entities are preserved.
   */
  @Test
  public void testModels() {
    // Create a person model object
    Person person = new Person("firstName", "lastName", "email@email.com", utils.FormUtils.hashPassword("password"),
                               "state", "city", "zip", "streetName", "streetNumber");

    // Create a measurement model object
    Measurement measurement = new Measurement(1L, 120.0, 60.0);

    // Create an external event model object
    ExternalEvent externalEvent = new ExternalEvent("Weather", "Flossie", 1L, 2L);

    // Create an alert model object
    Alert alert = new Alert(Alert.AlertType.FREQUENCY, 57.0, 1L, 2L);

    // Create an alert notification model object
    AlertNotification alertNotification = new AlertNotification(true, true, true, true, "AT&T", "5555555555",
                                                                "email@email.com", 58.0, 62.0, 158.0, 162.0);

    // Create an OpqDevice model object
    OpqDevice opqDevice = new OpqDevice(0x0123456789ABCDEFL, "description", "Hawaii");

    // Associate external event with alert
    externalEvent.getAlerts().add(alert);
    alert.setExternalEvent(externalEvent);

    // Associate the person with the device.
    person.getDevices().add(opqDevice);
    opqDevice.setPerson(person);

    // Associate the measurement with the device
    measurement.setDevice(opqDevice);
    opqDevice.getMeasurements().add(measurement);

    // Associate alert notification with device
    alertNotification.setDevice(opqDevice);
    opqDevice.getAlertNotifications().add(alertNotification);

    // Associate alert with device
    alert.setDevice(opqDevice);
    opqDevice.getAlerts().add(alert);

    // Persist everything to the DB
    opqDevice.save();
    person.save();
    measurement.save();
    alertNotification.save();
    alert.save();
    externalEvent.save();

    // Retrieve all items from the database
    List<OpqDevice> opqDevices = OpqDevice.find().all();
    List<Person> persons = Person.find().all();
    List<Measurement> measurements = Measurement.find().all();
    List<AlertNotification> alertNotifications  = AlertNotification.find().all();
    List<Alert> alerts = Alert.find().all();
    List<ExternalEvent> externalEvents = ExternalEvent.find().all();

    // Check that each list contains a single item
    assertEquals("Checking devices size", opqDevices.size(), 1);
    assertEquals("Checking persons size", persons.size(), 1);
    assertEquals("Checking measurements size", measurements.size(), 1);
    assertEquals("Checking alert notifications size", alertNotifications.size(), 1);
    assertEquals("Checking alerts size", alerts.size(), 1);
    assertEquals("Checking external events size", externalEvents.size(), 1);

    // Check to make sure we've recovered all relationships
    assertEquals("Person-OpqDevice", persons.get(0).getDevices().get(0), opqDevices.get(0));
    assertEquals("OpqDevice-Person", opqDevices.get(0).getPerson(), persons.get(0));
    assertEquals("Measurement-OpqDevice", measurements.get(0).getDevice(), opqDevices.get(0));
    assertEquals("OpqDevice-Measurement", opqDevices.get(0).getMeasurements().get(0), measurements.get(0));
    assertEquals("AlertNotification-OpqDevice", alertNotifications.get(0).getDevice(), opqDevices.get(0));
    assertEquals("OpqDevice-AlertNotification", opqDevices.get(0).getAlertNotifications().get(0),
                 alertNotifications.get(0));
    assertEquals("Alert-OpqDevice", alerts.get(0).getDevice(), opqDevices.get(0));
    assertEquals("OpqDevice-Alert", opqDevices.get(0).getAlerts().get(0), alerts.get(0));
    assertEquals("Alert-OpqDevice", alerts.get(0).getDevice(), opqDevices.get(0));
    assertEquals("Alert-ExternalEvent", alerts.get(0).getExternalEvent(), externalEvents.get(0));
    assertEquals("ExternalEvent-Alert", externalEvents.get(0).getAlerts().get(0), alerts.get(0));
  }
}
