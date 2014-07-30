/*
  This file is part of OPQHub.

  OPQHub is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  OPQHub is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with OPQHub.  If not, see <http://www.gnu.org/licenses/>.

  Copyright 2014 Anthony Christe
 */

package controllers;

import jobs.HeartbeatAlertActor;
import models.Event;
import models.EventData;
import models.AccessKey;
import models.Location;
import models.OpqDevice;
import org.openpowerquality.protocol.JsonOpqPacketFactory;
import org.openpowerquality.protocol.OpqPacket;
import play.Logger;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import utils.DateUtils;
import utils.Mailer;

import java.util.HashMap;
import java.util.Map;


/**
 * Provides methods for handling packets sent to this server from a WebSockets client.
 */
public class WebSockets extends Controller {
  private static final Map<AccessKey, WebSocket.Out<String>> keyToOut = new HashMap<>();

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
            //System.out.println("recv: " + s);
            OpqPacket opqPacket = JsonOpqPacketFactory.opqPacketFromBase64EncodedJson(s);
            //opqPacket.reverseBytes();
            //System.out.println(opqPacket);
            Logger.info(String.format("Received %s from %s", opqPacket.packetType, opqPacket.deviceId));
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

  public static Result sendToDevice(Long deviceId, String message) {
    Logger.debug("Send to device [%d] %s", deviceId, message);
    OpqPacket packet = new OpqPacket();
    if(keyToOut.containsKey(deviceId)) {
      /*
      packet.setHeader();
      packet.setType(OpqPacket.PacketType.SETTING);
      packet.setSequenceNumber(0);
      packet.setDeviceId(deviceId);
      packet.setTimestamp(DateUtils.getMillis());
      packet.setBitfield(0);
      packet.setPayload(message.getBytes());
      packet.computeChecksum();
      deviceIdToOut.get(deviceId).write(packet.getBase64Encoding());*/
    }
    return redirect(controllers.routes.Application.index());
  }

  /**
   * Determines the type of packet that was received from the WebSocket, and calls the correct sub-handler.
   *
   * @param opqPacket The packet received from the WebSocket object.
   */
  private static void handlePacket(OpqPacket opqPacket, final WebSocket.Out<String> out) {

    Map<String, Object> queryMap = new HashMap<>();
    queryMap.put("deviceId", opqPacket.deviceId);
    queryMap.put("accessKey", opqPacket.deviceKey);
    AccessKey accessKey = AccessKey.find().where().allEq(queryMap).findUnique();

    if(accessKey == null) {
      Logger.error(String.format("null key lookup for packet %s", opqPacket));
      return;
    }

    // Update connection mapping and heartbeat monitor
    keyToOut.put(accessKey, out);
    HeartbeatAlertActor.update(accessKey, opqPacket.timestamp);

    // Update the device
    OpqDevice opqDevice = accessKey.getOpqDevice();
    if(opqDevice == null) {
      Logger.error(String.format("null opqDevice lookup for packet %s", opqPacket));
      return;
    }
    opqDevice.setLastHeartbeat(opqPacket.timestamp);
    opqDevice.update();

    // Update event
    Event event = new Event(opqPacket.timestamp, opqPacket.packetType, opqPacket.frequency, opqPacket.voltage,
                            opqPacket.duration);

    Location location = (Location) opqDevice.getLocation();
    if(location == null) {
      Logger.error(String.format("null location lookup for packet %s", opqPacket));
      return;
    }
    event.setAccessKey(accessKey);
    event.setLocation(location);
    location.getEvents().add(event);
    location.update();

    //TODO: This is hacky, fix it
    // Update event data
    StringBuilder sb = new StringBuilder();
    for(Double d : opqPacket.payload) {
      //System.out.println(d);
      sb.append(d);
      sb.append(",");
    }

    String rawPowerStr = sb.toString();

    EventData eventData = new EventData(rawPowerStr);
    eventData.setEvent(event);
    eventData.save();
    event.setEventData(eventData);
    event.save();

    // Update key
    accessKey.getEvents().add(event);
    accessKey.update();

    switch(opqPacket.packetType) {
      case EVENT_DEVICE:
      case EVENT_FREQUENCY:
      case EVENT_VOLTAGE:
        Mailer.sendAlerts(accessKey.getPersons(), String.format("OPQ %s", opqPacket.packetType.getName()),
                          String.format("Received alert from %d at %s [%s, %f V, %f Hz]\n",
                                        opqPacket.deviceId,
                                        DateUtils.toDateTime(opqPacket.timestamp),
                                        opqPacket.packetType.getName(),
                                        opqPacket.voltage,
                                        opqPacket.frequency));
        break;
      default:
        break;
    }
  }

    /*
    StringBuilder sb = new StringBuilder();
    for(Double d : opqPacket.getRawPowerData()) {
      //System.out.println(d);
      sb.append(d);
      sb.append(",");
    }

    String rawPowerStr = sb.toString();*/

  private static void handleDisconnect(WebSocket.Out<String> out) {
    for(AccessKey accessKey : keyToOut.keySet()) {
      if(keyToOut.get(accessKey).equals(out)) {
        Logger.debug(String.format("Removing [%d] from id->connection mapping", accessKey));
        keyToOut.remove(accessKey);
      }
    }
  }
}
