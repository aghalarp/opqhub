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

import models.Event;
import models.AlertNotification;
import models.ExternalEvent;
import models.Measurement;
import models.OpqDevice;
import models.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.FakeApplication;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;
import static play.test.Helpers.stop;

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
    Person person = new Person("firstName", "lastName", "email@email.com", utils.FormUtils.hashPassword("password", new byte[] {1, 2}),
                               "state", "city", "zip", "streetName", "streetNumber");

    // Create a measurement model object
    Measurement measurement = new Measurement(1L, 120.0, 60.0);

    // Create an external event model object
    ExternalEvent externalEvent = new ExternalEvent("Weather", "Flossie", 1L, 2L);

    OpqDevice opqDevice = new OpqDevice("1111-1111-1111-111", "description", "Hawaii");

    // Create an event model object
    Alert event = new Event(opqDevice, Event.AlertType.FREQUENCY, 1L, 1L, 1.0);

    // Create an event notification model object
    AlertNotification alertNotification = new AlertNotification(true, true, true, true, "AT&T", "5555555555",
                                                                "email@email.com", 58.0, 62.0, 158.0, 162.0);

    // Create an OpqDevice model object


    // Associate external event with event
    externalEvent.getEvents().add(event);
    event.setExternalEvent(externalEvent);

    // Associate the person with the device.
    person.getDevices().add(opqDevice);
    opqDevice.setPerson(person);

    // Associate the measurement with the device
    measurement.setDevice(opqDevice);
    opqDevice.getMeasurements().add(measurement);

    // Associate event notification with device
    alertNotification.setDevice(opqDevice);
    opqDevice.getAlertNotifications().add(alertNotification);

    // Associate event with device
    event.setDevice(opqDevice);
    opqDevice.getEvents().add(event);

    // Persist everything to the DB
    opqDevice.save();
    person.save();
    measurement.save();
    alertNotification.save();
    event.save();
    externalEvent.save();

    // Retrieve all items from the database
    List<OpqDevice> opqDevices = OpqDevice.find().all();
    List<Person> persons = Person.find().all();
    List<Measurement> measurements = Measurement.find().all();
    List<AlertNotification> alertNotifications  = AlertNotification.find().all();
    List<Event> events = Event.find().all();
    List<ExternalEvent> externalEvents = ExternalEvent.find().all();

    // Check that each list contains a single item
    assertEquals("Checking devices size", opqDevices.size(), 1);
    assertEquals("Checking persons size", persons.size(), 1);
    assertEquals("Checking measurements size", measurements.size(), 1);
    assertEquals("Checking event notifications size", alertNotifications.size(), 1);
    assertEquals("Checking events size", events.size(), 1);
    assertEquals("Checking external events size", externalEvents.size(), 1);

    // Check to make sure we've recovered all relationships
    assertEquals("Person-OpqDevice", persons.get(0).getDevices().get(0), opqDevices.get(0));
    assertEquals("OpqDevice-Person", opqDevices.get(0).getPerson(), persons.get(0));
    assertEquals("Measurement-OpqDevice", measurements.get(0).getDevice(), opqDevices.get(0));
    assertEquals("OpqDevice-Measurement", opqDevices.get(0).getMeasurements().get(0), measurements.get(0));
    assertEquals("AlertNotification-OpqDevice", alertNotifications.get(0).getDevice(), opqDevices.get(0));
    assertEquals("OpqDevice-AlertNotification", opqDevices.get(0).getAlertNotifications().get(0),
                 alertNotifications.get(0));
    assertEquals("Event-OpqDevice", events.get(0).getDevice(), opqDevices.get(0));
    assertEquals("OpqDevice-Event", opqDevices.get(0).getEvents().get(0), events.get(0));
    assertEquals("Event-OpqDevice", events.get(0).getDevice(), opqDevices.get(0));
    assertEquals("Event-ExternalEvent", events.get(0).getExternalEvent(), externalEvents.get(0));
    assertEquals("ExternalEvent-Event", externalEvents.get(0).getEvents().get(0), events.get(0));
  }
}
