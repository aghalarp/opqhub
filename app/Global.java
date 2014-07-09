import akka.actor.ActorRef;
import akka.actor.Props;
import play.Application;
import play.GlobalSettings;
import play.data.format.Formatters;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import utils.Sms;
import views.html.helper.input;

import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Global extends GlobalSettings {
  @Override
  public void onStart(Application app) {
    Formatters.register(Sms.SmsCarrier.class, new Formatters.SimpleFormatter<Sms.SmsCarrier>() {
      @Override
      public Sms.SmsCarrier parse(String text, Locale locale) throws ParseException {
        return Sms.getCarrierByName(text);
      }

      @Override
      public String print(Sms.SmsCarrier smsCarrier, Locale locale) {
        return smsCarrier.getName();
      }
    });

    // Start up device health monitoring
    ActorRef actor = Akka.system().actorOf(new Props(jobs.HeartbeatAlertActor.class));
    Akka.system().scheduler().schedule(
        Duration.create(0, TimeUnit.MILLISECONDS),
        Duration.create(5, TimeUnit.SECONDS),
        actor,
        "hello, world",
        Akka.system().dispatcher(),
        null);
  }
}
