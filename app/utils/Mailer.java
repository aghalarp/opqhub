package utils;


import models.Person;
import org.openpowerquality.protocol.OpqPacket;

import org.apache.commons.mail.*;
import play.Logger;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static play.libs.Akka.future;

public class Mailer {
  private static String host = play.Play.application().configuration().getString("smtp.host");
  private static int port = play.Play.application().configuration().getInt("smtp.port");
  private static String user = play.Play.application().configuration().getString("smtp.user");
  private static String pass = play.Play.application().configuration().getString("smtp.pass");
  private static boolean ssl = play.Play.application().configuration().getBoolean("smtp.ssl");

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

  public static void sendAlerts(Set<Person> persons, String subject, String body) {
    String alertEmail;
    Sms.SmsCarrier smsCarrier;
    String smsNumber;

    for(Person person : persons) {
      alertEmail = person.getAlertEmail();
      smsCarrier = person.getSmsCarrier();
      smsNumber = person.getSmsNumber();

      if(alertEmail != null) {
        Mailer.sendEmail(alertEmail, subject, body);
      }

      if(smsCarrier != null || smsNumber != null) {
        Mailer.sendEmail(Sms.formatSmsEmailAddress(smsNumber,smsCarrier), subject, body);
      }
    }
  }
}
