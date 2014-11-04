var spinner;
var spinnerTarget = document.getElementById('main-content');

/**
 * Manages the web socket connection with the cloud.
 * @type {{websocket: null, init: init, requestUpdate: requestUpdate, requestDetails: requestDetails, onMessage: onMessage}}
 */
var ws = {
  websocket: null,

  spinner: null,

  /**
   * Open a new WS connection with the passed in address.
   * @param addr The address of the WS server.
   */
  init: function(addr, spinner) {
    // Create connection
    ws.websocket = new WebSocket(addr, "protocolOne");
    ws.spinner = spinner;
    // Setup callbacks
    ws.websocket.onopen = function() {
      ws.requestUpdate();
    };
    ws.websocket.onmessage = ws.onMessage;
  },

  /**
   * Collects fields from the filter object and then makes a request for new data from the cloud.
   */
  requestUpdate: function() {
    if (ws.websocket) {
      ws.spinner.spin(document.getElementById('main-content'));
      var json = filters.toJson();
      json.packetType = "private-update";
      ws.websocket.send(JSON.stringify(json));
    }
  },

  /**
   * Makes a request for details of a particular event.
   * @param rowIndex
   */
  requestDetails: function(rowIndex) {
    if(ws.websocket) {
      var json = JSON.stringify({
        packetType: "private-event-details",
        pk: events.tickerToKey[rowIndex]
      });
      ws.websocket.send(json);
    }
  },

  /**
   * Handles receiving messages from the cloud.
   * @param event
   */
  onMessage: function(event) {
    var data = JSON.parse(event.data);
    switch(data.packetType) {
      // Update the entire page
      case "private-map-response":
        ws.spinner.stop();
        events.update(data);
        filters.updateDefaults({
          frequencyGt: data['minFrequency'],
          frequencyLt: data['maxFrequency'],
          voltageGt: data['minVoltage'],
          voltageLt: data['maxVoltage'],
          durationGt: 0,
          durationLt: data['maxDuration'],
          requestFrequency: true,
          requestVoltage: true,
          requestIticSevere: true,
          requestIticModerate: true,
          requestIticOk: true,
          startTimestamp: data['minTimestamp'],
          stopTimestamp: data['maxTimestamp']
        });
        break;
      // Update event details
      case "private-event-response":
        details.show(data);
        break;
      // Unknown packet type
      default:
        console.log("Unknown packet type: " + data.packetType);
        break;
    }
  }
};


/**
 * Manages the events pane.
 * @type {{tickerToKey: {}, update: update, add: add, clear: clear}}
 */
var events = {
  /**
   * Store the row number of each event in the ticker and match it with its primary key.
   * @type {{string, integer}}
   */
  tickerToKey: {},

  /**
   * Update events table with new set of events from server.
   * @param data Set of events from server.
   */
  update: function(data) {
    // Remove old events
    events.clear();
    for (var event in data.events) {
      if(data.events.hasOwnProperty(event)) {
        events.add(data.events[event]);

        // Row index in HTML tables start at 1 instead of 0.
        events.tickerToKey[parseInt(event) + 1] = data.events[event].id;
      }
    }

    // Update statistics
    $("#totalEvents").text(data.totalEvents);
    $("#totalFrequencyEvents").text(data.totalFrequencyEvents);
    $("#totalVoltageEvents").text(data.totalVoltageEvents);

    // Handle click events on event ticker
    $('#events').find('> tbody > tr').click(function() {
      ws.requestDetails(this.rowIndex);
    });

    // Show event details for most recent event in ticker
    ws.requestDetails(1);
  },

  /**
   * Adds a new event to the top of events table.
   * @param event Event to add to events table.
   */
  add: function(event) {
    var eventValue;
    var iticRegion = itic.itic.getRegionOfPoint(event.duration * 1000, itic.itic.voltageToPercentNominalVoltage(event.voltage));

    // Get the event value and proper units
    switch(event.type) {
      case "Frequency":
        eventValue = event.frequency + " Hz";
        break;
      case "Voltage":
        eventValue = event.voltage + " V";
        break;
      default:
        eventValue = "N/A";
        break;
    }

    // Generate new HTML row and add it to the events table
    var row = html.tr(html.td(event.timestamp) + html.td(event.type) + html.td(event.duration + " s") +
      html.td(eventValue) + html.td(html.makeBadge(iticRegion)));
    $(row).appendTo("#events > tbody");
  },

  /**
   * Remove all events from event table and clear associated event->event-pk relationship.
   */
  clear: function() {
    $("#events").find("> tbody").children().remove();
    while(events.tickerToKey.length > 0) {
      events.tickerToKey.pop();
    }
  }
};

/**
 * Manages the event details.
 * @type {{init: init, show: show, updateWaveform: updateWaveform}}
 */
var details = {
  /**
   * Set up the details pane and any action listeners.
   */
  init: function() {

  },

  /**
   * Display the details for a particular event.
   * @param event
   */
  show: function(event) {
    // Update table
    var iticRegion = itic.itic.getRegionOfPoint(event.duration * 1000, itic.itic.voltageToPercentNominalVoltage(event.voltage));
    $("#event-title").text("Event Details - " + event.timestamp + " (" + event.eventType + ")");
    $("#details-device-id").text(event.deviceId);
    $("#details-device-description").text(event.deviceDescription);
    $("#details-frequency").text(event.frequency);
    $("#details-voltage").text(event.voltage);
    $("#details-duration").text(event.duration);
    $("#details-itic-title").html("ITIC" + html.makeBadge(iticRegion));
    $("#details-itic-severity").html(iticRegion);
    $("#details-event-level").text("Local");
    $("#details-grid-id").text(event.gridId);

    // Update waveform
    var points = [[]];
    var min = Number.MAX_VALUE;
    var max = Number.MIN_VALUE;
    var point;
    for(var i = 0; i < event.waveform.length; i++) {
      point = event.waveform[i];
      points[0].push([i, point]);
      min = point < min ? point : min;
      max = point > max ? point : max;
    }
    details.updateWaveform(points, min, max);
  },

  /**
   * Updates the waveform plot in the event details.
   * @param points List of points [x,y] to plot.
   * @param min The minimum y-point in the data.
   * @param max The maximum y-point in the data.
   */
  updateWaveform: function(points, min, max) {
    var plotOptions = {
      min: min - 0,
      max: max + 0,
      zoom: {
        interactive: true
      },
      pan: {
        interactive: true
      },
      acisLabels: {
        show: true
      },
      xaxis: {
        ticks: 5,
        min: 1000,
        max: 3000
      },
      xaxes: [{
        axisLabel: "Samples"
      }],
      yaxes: [{
        axisLabel: "Voltage"
      }],
      series: {
        lines: {show: true},
        points: {show: false}
      }
    };
    $.plot($("#private-waveform"), points, plotOptions);
  }
};

/**
 * Contains a reference to the ITIC class.
 * This allows easy access to the itic region finder and also encapsulates the reference in its own namespace.
 * @type {{itic: (iticPlotter|*)}}
 */
var itic = {
  itic: iticPlotter
};

/**
 * Various HTML utils for dynamically generating HTML.
 * @type {{td: td, tr: tr, getBadgeClass: getBadgeClass, makeBadge: makeBadge}}
 */
var html = {
  /**
   * Wrap the body with "td" tags.
   * @param body Contents to wrap in tags.
   * @returns {string} Html of body wrapped in td tags.
   */
  td: function(body) {return "<td>" + body + "</td>";},

  /**
   * Wrap the body with "tr" tags.
   * @param body Content to wrap in tags.
   * @returns {string} Html of the body wrapped in tr tags.
   */
  tr: function(body) {return "<tr>" + body + "</tr>";},

  /**
   * Returns the CSS class that corresponds to a particular ITIC region use for styling colored circle badges.
   * @param iticRegion The region to find a corresponding CSS class for.
   * @returns {*} The CSS class that corresponds to this ITIC region.
   */
  getBadgeClass: function(iticRegion) {
    var badgeClass;
    switch(iticRegion) {
      case itic.itic.Region.NO_INTERRUPTION:
        badgeClass = "itic-no-interruption";
        break;
      case itic.itic.Region.NO_DAMAGE:
        badgeClass = "itic-no-damage";
        break;
      case itic.itic.Region.PROHIBITED:
        badgeClass = "itic-prohibited";
        break;
      default:
        badgeClass = "";
        break;
    }
    return badgeClass;
  },

  /**
   * Dynamically generate a bootstrap 3 badge with a color that corresponds to the ITIC region.
   * @param iticRegion ITIC region to generate a badge for.
   * @returns {string} Dynamically generated bootstrap 3 badge.
   */
  makeBadge: function(iticRegion) {
    return "<span class='badge itic-badge " + html.getBadgeClass(iticRegion) +"'>&nbsp;</span>";
  }
};

/**
 * Entry point into this script.
 */
function initPage(serverWs) {
  var opts = {
    lines: 7, // The number of lines to draw
    length: 20, // The length of each line
    width: 10, // The line thickness
    radius: 16, // The radius of the inner circle
    corners: 1, // Corner roundness (0..1)
    rotate: 0, // The rotation offset
    direction: 1, // 1: clockwise, -1: counterclockwise
    color: '#000', // #rgb or #rrggbb or array of colors
    speed: 1, // Rounds per second
    trail: 60, // Afterglow percentage
    shadow: false, // Whether to render a shadow
    hwaccel: false, // Whether to use hardware acceleration
    className: 'spinner', // The CSS class to assign to the spinner
    zIndex: 2e9, // The z-index (defaults to 2000000000)
    top: '65%', // Top position relative to parent
    left: '50%' // Left position relative to parent
  };
  var target = document.getElementById('main-content');
  var spinner = new Spinner(opts).spin(target);

  details.init();
  filters.init();
  ws.init(serverWs, spinner);
}
