package jobs;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import jobs.reports.EventReports;
import jobs.support_classes.DeviceStats;
import jobs.support_classes.HtmlMailerMessage;
import jobs.support_classes.PersonDeviceInfo;
import utils.DateUtils;

import java.util.Date;
import java.util.Map;

/**
 * Actor responsible for collecting event report data (from helper class EventReports).
 * Spawns child actors responsible for sending Html formatted emails to users/persons.
 */
public class EventReportActor extends UntypedActor {
    public static enum Message {
        DAILY_REPORT, WEEKLY_REPORT, MONTHLY_REPORT, FULL_REPORT;
    }

    @Override
    public void onReceive(Object msg) {
        if (msg == Message.DAILY_REPORT) {
            // Calculate Daily timestamp
            long nowTimestamp = new Date().getTime();
            long dayAgoTimestamp = DateUtils.getPastTime(nowTimestamp, DateUtils.TimeUnit.Day);

            //Get Daily Event Data
            Map<Long, PersonDeviceInfo> map = EventReports.generateReportFromTimeFrame(dayAgoTimestamp, nowTimestamp);

            for (PersonDeviceInfo pdi : map.values()) {
                // Create Html template string.
                String htmlTemplate = buildEventReportHtmlTemplate(pdi);

                // Create message object to be sent to HtmlMailerActor
                final HtmlMailerMessage htmlMessage = new HtmlMailerMessage("aghalarp@gmail.com", htmlTemplate);
                //final HtmlMailerMessage htmlMessage = new HtmlMailerMessage(pdi.getPersonEmail(), htmlTemplate);

                //Create HtmlMailerActor and send message.
                final ActorRef htmlMailer = getContext().actorOf(Props.create(jobs.HtmlMailerActor.class), "htmlMailer");
                htmlMailer.tell(htmlMessage, getSelf());
            }

        }
        else if (msg.equals(Message.WEEKLY_REPORT)) {
            //Calculate Weekly timestamp
            long nowTimestamp = new Date().getTime();
            long weekAgoTimestamp = DateUtils.getPastTime(nowTimestamp, DateUtils.TimeUnit.Week);

            //Get Weekly Event Data
            Map<Long, PersonDeviceInfo> map = EventReports.generateReportFromTimeFrame(weekAgoTimestamp, nowTimestamp);

            for (PersonDeviceInfo pdi : map.values()) {
                // Create Html template string.
                String htmlTemplate = buildEventReportHtmlTemplate(pdi);

                // Create message object to be sent to HtmlMailerActor
                final HtmlMailerMessage htmlMessage = new HtmlMailerMessage("aghalarp@gmail.com", htmlTemplate);
                //final HtmlMailerMessage htmlMessage = new HtmlMailerMessage(pdi.getPersonEmail(), htmlTemplate);

                //Create HtmlMailerActor and send message.
                final ActorRef htmlMailer = getContext().actorOf(Props.create(jobs.HtmlMailerActor.class), "htmlMailer");
                htmlMailer.tell(htmlMessage, getSelf());
            }
        }
        else if (msg.equals(Message.MONTHLY_REPORT)) {
            //Calculate Monthly timestamp
            long nowTimestamp = new Date().getTime();
            long monthAgoTimestamp = DateUtils.getPastTime(nowTimestamp, DateUtils.TimeUnit.Month);

            //Get Monthly Event Data
            Map<Long, PersonDeviceInfo> map = EventReports.generateReportFromTimeFrame(monthAgoTimestamp, nowTimestamp);

            for (PersonDeviceInfo pdi : map.values()) {
                // Create Html template string.
                String htmlTemplate = buildEventReportHtmlTemplate(pdi);

                // Create message object to be sent to HtmlMailerActor
                final HtmlMailerMessage htmlMessage = new HtmlMailerMessage("aghalarp@gmail.com", htmlTemplate);
                //final HtmlMailerMessage htmlMessage = new HtmlMailerMessage(pdi.getPersonEmail(), htmlTemplate);

                //Create HtmlMailerActor and send message.
                final ActorRef htmlMailer = getContext().actorOf(Props.create(jobs.HtmlMailerActor.class), "htmlMailer");
                htmlMailer.tell(htmlMessage, getSelf());
            }
        }
        else if (msg.equals(Message.FULL_REPORT))  {
            //Get all data since beginning of time/universe. Or rather, the unix epoch.
            Map<Long, PersonDeviceInfo> map = EventReports.generateReportFromTimeFrame(0L, new Date().getTime());

            for (PersonDeviceInfo pdi : map.values()) {
                // Create Html template string.
                String htmlTemplate = buildEventReportHtmlTemplate(pdi);

                // Create message object to be sent to HtmlMailerActor
                final HtmlMailerMessage htmlMessage = new HtmlMailerMessage("aghalarp@gmail.com", htmlTemplate);
                //final HtmlMailerMessage htmlMessage = new HtmlMailerMessage(pdi.getPersonEmail(), htmlTemplate);

                //Create HtmlMailerActor and send message.
                final ActorRef htmlMailer = getContext().actorOf(Props.create(jobs.HtmlMailerActor.class));
                htmlMailer.tell(htmlMessage, getSelf());
            }
        }
        else {
            unhandled(msg);
        }
    }

    /**
     * Constructs an Html template with the given PersonDiveInfo data to be used for event report emails.
     *
     * @param pdi The PersonDeviceInfo object containing the data to be added to the Html template.
     * @return An html string formatted with the given PDI data.
     */
    private String buildEventReportHtmlTemplate(PersonDeviceInfo pdi) {

        StringBuilder sb = new StringBuilder();

        //Boilerplate Html template code, don't be overwhelmed.
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns=\"http://www.w3.org/1999/xhtml\" style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; margin: 0; padding: 0;\">\n" +
                "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width\" />\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "<title>OPQHub Event Report</title>\n" +
                "</head>\n" +
                "<body bgcolor=\"#f6f6f6\" style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; -webkit-font-smoothing: antialiased; -webkit-text-size-adjust: none; width: 100% !important; height: 100%; margin: 0; padding: 0;\">&#13;&#13;&#13;\n" +
                "<table style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; width: 100%; margin: 0; padding: 20px;\">\n" +
                "<tr style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; margin: 0; padding: 0;\"><td style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; margin: 0; padding: 0;\"></td>&#13;\n" +
                "<td bgcolor=\"#FFFFFF\" style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; display: block !important; max-width: 600px !important; clear: both !important; margin: 0 auto; padding: 20px; border: 1px solid #f0f0f0;\">&#13;&#13;&#13;\n" +
                "<div style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; max-width: 600px; display: block; margin: 0 auto; padding: 0;\">&#13;\n" +
                "<table style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; width: 100%; margin: 0; padding: 0;\">\n" +
                "<tr style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; margin: 0; padding: 0;\">\n" +
                "<td style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; margin: 0; padding: 0;\">&#13;");

        //Set H1 title here.
        sb.append("<h1 style=\"font-family: 'Helvetica Neue', Helvetica, Arial, 'Lucida Grande', " +
                "sans-serif; font-size: 36px; line-height: 1.2; color: #000; font-weight: 200; " +
                "margin: 10px 0 10px; padding: 0;\">OPQHub Event Report</h1>&#13;");

        //Set person name.
        sb.append("<h2 style=\"font-family: 'Helvetica Neue', Helvetica, Arial, 'Lucida Grande', sans-serif; " +
                "font-size: 24px; line-height: 1.2; color: #000; font-weight: 200; margin: 40px 0 10px; " +
                "padding: 0;\">User: " + pdi.getPersonLastName() + ", " + pdi.getPersonFirstName() + "</h2>&#13;");

        //Create table to hold device data.
        sb.append("<table cellpadding=\"5\" border=\"1\" style=\"border-collapse: collapse; border: 1px solid black;\">");
        sb.append("<tr><th>Device ID</th><th>Frequency Events</th><th>Voltage Events</th></tr>");

        for (DeviceStats stats : pdi.getDeviceStatsList()) {
            sb.append("<tr>");
            sb.append("<td>" + stats.getDeviceID() + "</td>");
            sb.append("<td>" + stats.getFreqEventCount() + "</td>");
            sb.append("<td>" + stats.getVoltEventCount() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");


        //More boilerplate Html to finish up template.
        sb.append("<table style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; width: 100%; margin: 0; padding: 0;\">\n" +
                "<tr style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; margin: 0; padding: 0;\">\n" +
                "<td style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; margin: 0; padding: 10px 0;\">&#13;\n" +
                "<p style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 1.6; font-weight: normal; margin: 20px 0 10px; padding: 0;\"><a href=\"http://emilia.ics.hawaii.edu:8194\" style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 2; color: #FFF; text-decoration: none; font-weight: bold; text-align: center; cursor: pointer; display: inline-block; border-radius: 25px; background-color: #348eda; margin: 0 10px 0 0; padding: 0; border-color: #348eda; border-style: solid; border-width: 10px 20px;\">View more at OPQHub</a></p>&#13;\n" +
                "</td>&#13;\n" +
                "</tr>\n" +
                "</table>\n" +
                "</td>&#13;\n" +
                "</tr>\n" +
                "</table>\n" +
                "</div>&#13;&#13;&#13;\n" +
                "</td>&#13;\n" +
                "<td style=\"font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif; font-size: 100%; line-height: 1.6; margin: 0; padding: 0;\"></td>&#13;\n" +
                "</tr>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>");


        String htmlTemplate = sb.toString(); // Create immutable string for actor message later.

        return htmlTemplate;
    }

}
