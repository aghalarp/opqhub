package utils;


import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;
import org.openpowerquality.protocol.OpqPacket;

import java.util.concurrent.Callable;

import static play.libs.Akka.future;

public class Mailer {

  public static void sendAlert(final OpqPacket opqPacket, final String to) {
    future(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
        mail.setSubject("OPQ Alert");
        mail.addFrom("OPQ Alert <openpowerquality@gmail.com>");
        mail.addRecipient(to);
        mail.send(String.format("Timestamp: %s\nAlert Type: %s\nAlert Value: %s",
                                utils.DateUtils.toDateTime(opqPacket.getTimestamp()),
                                opqPacket.getType().getName(),
                                opqPacket.getAlertValue()));
        return null;
      }
    });
  }
}
