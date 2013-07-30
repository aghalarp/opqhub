/*
  This file is part of opq-ao.

  opa-ao is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opa-ao is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opq-ao.  If not, see <http://www.gnu.org/licenses/>.

  Copyright 2013 Anthony Christe
 */

package utils;

public class FormUtils {
  private FormUtils() {
    throw new AssertionError("FormUtils should not be instantiated");
  }

  public static final String[] LIST_OF_STATES = {
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
  private static final String[][] SMS_CARRIERS = {
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
    String[] carriers = new String[SMS_CARRIERS.length];
    for (int i = 0; i < carriers.length; i++) {
      carriers[i] = SMS_CARRIERS[i][0];
    }
    return carriers;
  }

}
