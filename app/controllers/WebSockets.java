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
import models.Alert;
import models.Measurement;
import models.OpqDevice;
import org.openpowerquality.protocol.OpqPacket;
import play.Logger;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.WebSocket;

import java.util.HashMap;
import java.util.Map;


/**
 * Provides methods for handling packets sent to this server from a WebSockets client.
 */
public class WebSockets extends Controller {
  private static Map<Long, WebSocket.Out<String>> deviceIdToOut = new HashMap<>();

  /**
   * Create a WebSocket object who can receive connections, receive packets, and break connections.
   *
   * @return A WebSocket object.
   */
  public static WebSocket<String> handleSocket() {
    return new WebSocket<String>() {
      @Override
      public void onReady(In<String> in, final Out<String> out) {
        Logger.info("Websocket ready");

        in.onMessage(new F.Callback<String>() {
          @Override
          public void invoke(String s) throws Throwable {
            OpqPacket opqPacket = new OpqPacket(s);
            Logger.info(String.format("Received %s from %s", opqPacket.getType(), opqPacket.getDeviceId()));
            handlePacket(opqPacket, out);
          }
        });

        in.onClose(new F.Callback0() {
          @Override
          public void invoke() throws Throwable {
            handleDisconnect(out);
            Logger.info("Websocket disconnected");
          }
        });
      }
    };
  }

  /**
   * Determines the type of packet that was received from the WebSocket, and calls the correct sub-handler.
   *
   * @param opqPacket The packet received from the WebSocket object.
   */
  private static void handlePacket(OpqPacket opqPacket, final WebSocket.Out<String> out) {
    switch(opqPacket.getType()) {
      case EVENT_FREQUENCY:
      case EVENT_VOLTAGE:
      case EVENT_DEVICE:
        handleAlert(opqPacket);
        break;
      case MEASUREMENT:
        handleMeasurement(opqPacket);
        break;
      case PING:
        handlePing(opqPacket, out);
        break;
    }
  }

  /**
   * Handles receiving of alert packets from device.
   * <p/>
   * Once a valid alert is received, add it to the database.
   *
   * @param opqPacket Event packet from device.
   */
  private static void handleAlert(OpqPacket opqPacket) {
    Long deviceId = opqPacket.getDeviceId();
    OpqDevice opqDevice = OpqDevice.find().where().eq("deviceId", deviceId).findUnique();

    if(opqDevice == null) {
      Logger.warn("handleAlert opq device is null");
      return;
    }

    Event event = new Event(
        opqDevice,
        opqPacket.getType(),
        opqPacket.getTimestamp(),
        opqPacket.getEventDuration(),
        opqPacket.getEventValue());

    event.setDevice(opqDevice);
    event.save();
    opqDevice.getEvents().add(event);
    opqDevice.save();

    // Determine whether or not to notify user based on their alert preferences
    if(opqDevice.getAlerts().size() == 0) {
      return;
    }

    Alert alert = opqDevice.getAlerts().get(0);
    switch(opqPacket.getType()) {
      case EVENT_FREQUENCY:
        if(alert.getFrequencyAlertNotification() &&
           (opqPacket.getEventValue() < alert.getMinAcceptableFrequency() ||
            opqPacket.getEventValue() > alert.getMaxAcceptableFrequency())) {
            if(alert.getAlertViaEmail()) {
              utils.Mailer.sendAlert(opqPacket, alert.getNotificationEmail());
            }
            if(alert.getAlertViaSms()) {
              utils.Mailer.sendAlert(opqPacket, utils.Sms.formatSmsEmailAddress(alert.getSmsNumber(), alert.getSmsCarrier()));
            }
        }
        break;
      case EVENT_VOLTAGE:
        if(alert.getVoltageAlertNotification() &&
           (opqPacket.getEventValue() < alert.getMinAcceptableVoltage() ||
            opqPacket.getEventValue() > alert.getMaxAcceptableVoltage())) {
          if(alert.getAlertViaEmail()) {
            utils.Mailer.sendAlert(opqPacket, alert.getNotificationEmail());
          }
          if(alert.getAlertViaSms()) {
            utils.Mailer.sendAlert(opqPacket, utils.Sms.formatSmsEmailAddress(alert.getSmsNumber(), alert.getSmsCarrier()));
          }
        }
        break;
      case EVENT_DEVICE: break;
    }
  }

  /**
   * Handles receiving of measurement packets from device.
   * <p/>
   * When a valid measurement is received, that measurement is added to the database.
   *
   * @param opqPacket Measurement packet from device.
   */
  private static void handleMeasurement(OpqPacket opqPacket) {
    Long deviceId = opqPacket.getDeviceId();
    OpqDevice opqDevice = OpqDevice.find().where().eq("deviceId", deviceId).findUnique();

    if(opqDevice == null) {
      Logger.warn("handleMeasurement opq device is null");
      return;
    }

    Measurement measurement = new Measurement(
        opqPacket.getTimestamp(),
        opqPacket.getFrequency(),
        opqPacket.getVoltage()
    );

    measurement.setDevice(opqDevice);
    measurement.save();
    opqDevice.getMeasurements().add(measurement);
    opqDevice.save();
  }

  private static void handlePing(OpqPacket opqPacket, WebSocket.Out<String> out) {
    deviceIdToOut.put(opqPacket.getDeviceId(), out);
    out.write(opqPacket.getBase64Encoding());
  }

  private static void handleDisconnect(WebSocket.Out<String> out) {
    for(Long l : deviceIdToOut.keySet()) {
      if(deviceIdToOut.get(l).equals(out)) {
        deviceIdToOut.remove(l);
      }
    }
  }
}
