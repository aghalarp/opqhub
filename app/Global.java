import play.Application;
import play.GlobalSettings;
import play.data.format.Formatters;
import utils.Sms;
import views.html.helper.input;

import java.text.ParseException;
import java.util.Locale;


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
  }
}
