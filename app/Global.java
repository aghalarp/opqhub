import akka.actor.ActorRef;
import akka.actor.Props;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.event.ServerConfigStartup;
import jobs.EventReportActor;
import play.Application;
import play.GlobalSettings;
import play.data.format.Formatters;
import play.libs.Akka;
import play.Logger;
import scala.concurrent.duration.Duration;
import utils.Sms;

import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Global extends GlobalSettings implements ServerConfigStartup {
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
    Logger.debug("Starting akka system for scheduled heartbeat checks");
    ActorRef actor = Akka.system().actorOf(Props.create(jobs.HeartbeatAlertActor.class), "heartbeatActor");
    Akka.system().scheduler().schedule(
        Duration.create(0, TimeUnit.MILLISECONDS),
        Duration.create(10, TimeUnit.MINUTES),
        actor,
        "hello, world",
        Akka.system().dispatcher(),
        null);

    //Setup scheduled event report email system.
//    ActorRef mailerActor = Akka.system().actorOf(Props.create(EventReportActor.class));
//    Akka.system().scheduler().schedule(
//      Duration.create(0, TimeUnit.MILLISECONDS), //Initial delay
//      Duration.create(600, TimeUnit.SECONDS),     //Frequency
//      mailerActor,
//      EventReportActor.Message.FULL_REPORT,
//      Akka.system().dispatcher(),
//      null
//    );

  }

  @Override
  public void onStart(ServerConfig serverConfig) {
    serverConfig.setUpdateChangesOnly(true);
  }
}

