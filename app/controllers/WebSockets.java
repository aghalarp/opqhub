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

import play.libs.F;
import play.mvc.Controller;
import play.mvc.WebSocket;

import java.util.Arrays;

public class WebSockets extends Controller {

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

  private static void handlePacket(String packet) {
    switch(packet.split(",")[1]) {
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

  private static void handleAlert(String packet) {
    String[] alertParts = packet.split(",");
    System.out.println("Received alert...");
    System.out.println(Arrays.toString(alertParts));
    //models.Alert alert = new models.Alert();
  }

  private static void handleMeasurement(String packet) {
    String[] data = packet.split(",");
    System.out.println("Received measurement...");
    System.out.println(Arrays.toString(data));
  }
}
