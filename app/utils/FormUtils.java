package utils;

public class FormUtils {
  public static final String[] listOfStates = {
    "Alabama",
    "Alaska",
    "Arizona",
    "Arkansas",
    "California",
    "Colorado",
    "Connecticut",
    "Delaware",
    "Florida",
    "Georgia",
    "Hawaii",
    "Idaho",
    "Illinois",
    "Indiana",
    "Iowa",
    "Kansas",
    "Kentucky",
    "Louisiana",
    "Maine",
    "Maryland",
    "Massachusetts",
    "Michigan",
    "Minnesota",
    "Mississippi",
    "Missouri",
    "Montana",
    "Nebraska",
    "Nevada",
    "New Hampshire",
    "New Jersey",
    "New Mexico",
    "New York",
    "North Carolina",
    "North Dakota",
    "Ohio",
    "Oklahoma",
    "Oregon",
    "Pennsylvania",
    "Rhode Island",
    "South Carolina",
    "South Dakota",
    "Tennessee",
    "Texas",
    "Utah",
    "Vermont",
    "Virginia",
    "Washington",
    "West Virginia",
    "Wisconsin",
    "Wyoming"
  };
  // See: https://en.wikipedia.org/wiki/List_of_SMS_gateways
  public static final String[][] smsCarriers = {
    {"Alltel",        "#@sms.alltelwireless.com"},
    {"AT&T",          "#@txt.att.net"},
    {"Cricket",       "#@sms.myscricket.com"},
    {"Sprint",        "#@messaging.sprintpcs.com"},
    {"Straight Talk", "#@vtext.com"},
    {"T-Mobile",      "#@tmomail.net"},
    {"TracFone",      "#@mmst5.tracfone.com"},
    {"Verizon",       "#@vtext.com"},
    {"Virgin Mobile", "#@vmobl.com"}
  };

  public static String[] getSmsCarriers() {
    String[] carriers = new String[smsCarriers.length];
    for(int i = 0; i < carriers.length; i++) {
      carriers[i] = smsCarriers[i][0];
    }
    return carriers;
  }

}
