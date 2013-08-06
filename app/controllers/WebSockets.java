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
import play.Logger;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.WebSocket;

import java.util.Arrays;

/**
 * Provides methods for handling packets sent to this server from a WebSockets client.
 */
public class WebSockets extends Controller {
  public static final int DEVICE_ID = 0;
  public static final int PACKET_TYPE = 1;
  public static final int ALERT_TYPE = 2;
  public static final int TIMESTAMP = 3;
  public static final int DURATION = 4;
  public static final int ALERT_VALUE = 5;

  /**
   * Create a WebSocket object who can receive connections, receive packets, and break connections.
   *
   * @return A WebSocket object.
   */
  public static WebSocket<String> handleSocket() {
    return new WebSocket<String>() {
      @Override
      public void onReady(In<String> in, Out<String> out) {
        in.onMessage(new F.Callback<String>() {
          @Override
          public void invoke(String s) throws Throwable {
            handlePacket(s);
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
   * @param packet The packet received from the WebSocket object.
   */
  private static void handlePacket(String packet) {
    switch (packet.split(",")[PACKET_TYPE]) {
      case "A":
        handleAlert(packet);
        break;
      case "M":
        handleMeasurement(packet);
        break;
      default:
        System.out.println("Unknown packet type");
    }
  }

  /**
   * Handles receiving of alert packets from device.
   * <p/>
   * Once a valid alert is received, add it to the database.
   *
   * @param packet Alert packet from device.
   */
  private static void handleAlert(String packet) {
    String[] alertParts = packet.split(",");

    OpqDevice opqDevice = OpqDevice.find().where().eq("deviceId", alertParts[DEVICE_ID]).findUnique();
    Alert.AlertType alertType;

    switch (alertParts[ALERT_TYPE]) {
      case "D":
        alertType = Alert.AlertType.DEVICE;
        break;
      case "F":
        alertType = Alert.AlertType.FREQUENCY;
        break;
      case "V":
        alertType = Alert.AlertType.VOLTAGE;
        break;
      default:
        alertType = null;
        Logger.error("Unknown alert type " + alertParts[ALERT_TYPE]);
    }

    Long alertDuration = Long.parseLong(alertParts[DURATION]);
    Long timestamp = Long.parseLong(alertParts[TIMESTAMP]);
    Double alertValue = Double.parseDouble(alertParts[ALERT_VALUE]);

    Alert alert = new Alert(opqDevice, alertType, timestamp, alertDuration, alertValue);
    alert.save();
    opqDevice.getAlerts().add(alert);
    opqDevice.save();
  }

  /**
   * Handles receiving of measurement packets from device.
   * <p/>
   * When a valid measurement is received, that measurement is added to the database.
   *
   * @param packet Measurement packet from device.
   */
  private static void handleMeasurement(String packet) {
    String[] data = packet.split(",");
    System.out.println("Received measurement...");
    System.out.println(Arrays.toString(data));
    // TODO: Add to database.
  }
}
