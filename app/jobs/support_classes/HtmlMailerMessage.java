package jobs.support_classes;

/**
 * Actor message class used for passing data to HtmlMailerActor.
 * IMPORTANT: Because this class is being used as an actor message,
 * make sure it remains an immutable class.
 */
public class HtmlMailerMessage {
    private final String mailTo; // Email address.
    private final String htmlTemplate; // Complete Html to send in email.

    public HtmlMailerMessage(String mailTo, String htmlTemplate) {
        this.mailTo = mailTo;
        this.htmlTemplate = htmlTemplate;
    }

    public String getMailTo() {
        return mailTo;
    }

    public String getHtmlTemplate() {
        return htmlTemplate;
    }
}
