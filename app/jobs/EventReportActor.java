package jobs;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import jobs.reports.DeviceReports;
import jobs.support_classes.HtmlMailerMessage;
import jobs.support_classes.PersonDeviceInfo;
import play.api.templates.Html;
import utils.CssInliner;
import utils.DateUtils;
import views.html.emaileventreport;

import java.util.Date;
import java.util.Map;

/**
 * Actor responsible for collecting event report data (from helper class DeviceReports).
 * Spawns child actors responsible for sending Html formatted emails to users/persons.
 */
public class EventReportActor extends UntypedActor {

    private final ActorRef htmlMailerActor;

    public static enum Message {
        DAILY_REPORT, WEEKLY_REPORT, MONTHLY_REPORT, FULL_REPORT;
    }

    public EventReportActor() {
        this.htmlMailerActor = getContext().actorOf(Props.create(jobs.HtmlMailerActor.class), "htmlMailerActor");
    }

    @Override
    public void onReceive(Object msg) {
        if (msg == Message.DAILY_REPORT) {
            // Calculate Daily timestamp
            long nowTimestamp = new Date().getTime();
            long dayAgoTimestamp = DateUtils.getPastTime(nowTimestamp, DateUtils.TimeUnit.Day);

            //Get Daily Event Data
            Map<Long, PersonDeviceInfo> map = DeviceReports.generateAllDeviceReport(dayAgoTimestamp, nowTimestamp);

            for (PersonDeviceInfo pdi : map.values()) {
                // Create Html template string.
                Html htmlTemplate = emaileventreport.render(pdi);
                String htmlString = CssInliner.toInlinedCss(htmlTemplate.toString());

                // Create message object to be sent to HtmlMailerActor
                final HtmlMailerMessage htmlMessage = new HtmlMailerMessage("aghalarp@gmail.com", "OPQ Event Report", htmlString);
                //final HtmlMailerMessage htmlMessage = new HtmlMailerMessage(pdi.getPersonEmail(), htmlTemplate);

                //Create HtmlMailerActor and send message.
                final ActorRef htmlMailer = getContext().actorOf(Props.create(jobs.HtmlMailerActor.class));
                htmlMailer.tell(htmlMessage, getSelf());
            }

        }
        else if (msg.equals(Message.WEEKLY_REPORT)) {
            //Calculate Weekly timestamp
            long nowTimestamp = new Date().getTime();
            long weekAgoTimestamp = DateUtils.getPastTime(nowTimestamp, DateUtils.TimeUnit.Week);

            //Get Weekly Event Data
            Map<Long, PersonDeviceInfo> map = DeviceReports.generateAllDeviceReport(weekAgoTimestamp, nowTimestamp);

            for (PersonDeviceInfo pdi : map.values()) {
                // Create Html template string.
                Html htmlTemplate = emaileventreport.render(pdi);
                String htmlString = CssInliner.toInlinedCss(htmlTemplate.toString());

                // Create message object to be sent to HtmlMailerActor
                final HtmlMailerMessage htmlMessage = new HtmlMailerMessage("aghalarp@gmail.com", "OPQ Event Report", htmlString);
                //final HtmlMailerMessage htmlMessage = new HtmlMailerMessage(pdi.getPersonEmail(), htmlTemplate);

                //Create HtmlMailerActor and send message.
                final ActorRef htmlMailer = getContext().actorOf(Props.create(jobs.HtmlMailerActor.class));
                htmlMailer.tell(htmlMessage, getSelf());
            }
        }
        else if (msg.equals(Message.MONTHLY_REPORT)) {
            //Calculate Monthly timestamp
            long nowTimestamp = new Date().getTime();
            long monthAgoTimestamp = DateUtils.getPastTime(nowTimestamp, DateUtils.TimeUnit.Month);

            //Get Monthly Event Data
            Map<Long, PersonDeviceInfo> map = DeviceReports.generateAllDeviceReport(monthAgoTimestamp, nowTimestamp);

            for (PersonDeviceInfo pdi : map.values()) {
                // Create Html template string.
                Html htmlTemplate = emaileventreport.render(pdi);
                String htmlString = CssInliner.toInlinedCss(htmlTemplate.toString());

                // Create message object to be sent to HtmlMailerActor
                final HtmlMailerMessage htmlMessage = new HtmlMailerMessage("aghalarp@gmail.com", "OPQ Event Report", htmlString);
                //final HtmlMailerMessage htmlMessage = new HtmlMailerMessage(pdi.getPersonEmail(), htmlTemplate);

                //Create HtmlMailerActor and send message.
                final ActorRef htmlMailer = getContext().actorOf(Props.create(jobs.HtmlMailerActor.class));
                htmlMailer.tell(htmlMessage, getSelf());
            }
        }
        else if (msg.equals(Message.FULL_REPORT))  {
            //Get all data since beginning of time/universe. Or rather, the unix epoch.
            Map<Long, PersonDeviceInfo> map = DeviceReports.generateAllDeviceReport(0L, new Date().getTime());

            for (PersonDeviceInfo pdi : map.values()) {
                // Create Html template string.
                Html htmlTemplate = emaileventreport.render(pdi);
                String htmlString = CssInliner.toInlinedCss(htmlTemplate.toString());

                // Create message object to be sent to HtmlMailerActor
                final HtmlMailerMessage htmlMessage = new HtmlMailerMessage("aghalarp@gmail.com", "OPQ Event Report", htmlString);
                //final HtmlMailerMessage htmlMessage = new HtmlMailerMessage(pdi.getPersonEmail(), htmlTemplate);

                //Create HtmlMailerActor and send message.
                //final ActorRef htmlMailer = getContext().actorOf(Props.create(jobs.HtmlMailerActor.class), "htmlMailerActor");
                this.htmlMailerActor.tell(htmlMessage, getSelf());
            }
        }
        else {
            unhandled(msg);
        }
    }


}
