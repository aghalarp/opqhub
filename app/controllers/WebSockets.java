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
