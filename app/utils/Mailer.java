package utils;


import org.openpowerquality.protocol.OpqPacket;

import org.apache.commons.mail.*;
import play.Logger;

import java.util.concurrent.Callable;

import static play.libs.Akka.future;

public class Mailer {
  private static String host = play.Play.application().configuration().getString("smtp.host");
  private static int port = play.Play.application().configuration().getInt("smtp.port");
  private static String user = play.Play.application().configuration().getString("smtp.user");
  private static String pass = play.Play.application().configuration().getString("smtp.pass");
  private static boolean ssl = play.Play.application().configuration().getBoolean("smtp.ssl");

  public static void sendAlert(final OpqPacket opqPacket, final String to) {
  /*  future(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        Logger.info(String.format("mailer -> %s", to));
        MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
        mail.setSubject("OPQ Alert");
        mail.addFrom("OPQ Alert <openpowerquality@gmail.com>");
        mail.addRecipient(to);
        mail.send(String.format("Timestamp: %s\nEvent Type: %s\nEvent Value: %s",
                                utils.DateUtils.toDateTime(opqPacket.getTimestamp()),
                                opqPacket.getType().getName(),
                                opqPacket.getEventValue()));
        return null;
      }
    });*/
  }

  public static void sendEmail(final String to, final String subject, final String body) {
    future(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        Email email = new SimpleEmail();
        email.setHostName(host);
        email.setSmtpPort(port);
        email.setAuthenticator(new DefaultAuthenticator(user, pass));
        email.setSSLOnConnect(ssl);

        email.setFrom("alert@openpowerquality.com");
        email.setSubject(subject);
        email.setMsg(body);
        email.addTo(to);
        email.send();

        return null;
      }
    });
  }
}
