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
import models.Measurement;
import models.OpqDevice;
import org.openpowerquality.protocol.OpqPacket;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.WebSocket;


/**
 * Provides methods for handling packets sent to this server from a WebSockets client.
 */
public class WebSockets extends Controller {


  /**
   * Create a WebSocket object who can receive connections, receive packets, and break connections.
   *
   * @return A WebSocket object.
   */
  public static WebSocket<String> handleSocket() {
    return new WebSocket<String>() {
      @Override
      public void onReady(In<String> in, final Out<String> out) {

        in.onMessage(new F.Callback<String>() {
          @Override
          public void invoke(String s) throws Throwable {
            handlePacket(new OpqPacket(s));
          }
        });

        in.onClose(new F.Callback0() {
          @Override
          public void invoke() throws Throwable {
            System.out.println("Disconnected");
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
  private static void handlePacket(OpqPacket opqPacket) {
    switch(opqPacket.getType()) {
      case ALERT_FREQUENCY:
      case ALERT_VOLTAGE:
      case ALERT_DEVICE:
        handleAlert(opqPacket);
        break;
      case MEASUREMENT:
        handleMeasurement(opqPacket);
        break;
    }
  }

  /**
   * Handles receiving of alert packets from device.
   * <p/>
   * Once a valid alert is received, add it to the database.
   *
   * @param opqPacket Alert packet from device.
   */
  private static void handleAlert(OpqPacket opqPacket) {
    Long deviceId = opqPacket.getDeviceId();
    OpqDevice opqDevice = OpqDevice.find().where().eq("deviceId", deviceId).findUnique();

    if(opqDevice == null) {
      System.out.println("Device is null");
      return;
    }

    Alert alert = new Alert(
        opqDevice,
        opqPacket.getType(),
        opqPacket.getTimestamp(),
        opqPacket.getAlertDuration(),
        opqPacket.getAlertValue());

    alert.setDevice(opqDevice);
    alert.save();
    opqDevice.getAlerts().add(alert);
    opqDevice.save();
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
      System.out.println("Device is null");
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
}
