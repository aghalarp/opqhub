/**
 * Manages the web socket connection with the cloud.
 * @type {{websocket: null, init: init, requestUpdate: requestUpdate, requestDetails: requestDetails, onMessage: onMessage}}
 */
var ws = {
  websocket: null,

  /**
   * Open a new WS connection with the passed in address.
   * @param addr The address of the WS server.
   */
  init: function(addr) {
    // Create connection
    ws.websocket = new WebSocket(addr, "protocolOne");

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
      // Bind the filters from the view
      filters.update();
      var json = JSON.stringify({
        packetType: "public-update",
        requestFrequency: filters.requestFrequency,
        requestVoltage: filters.requestVoltage,
        requestHeartbeats: filters.requestHeartbeats,
        requestIticSevere: filters.requestIticSevere,
        requestIticModerate: filters.requestIticModerate,
        requestIticOk: filters.requestIticOk,
        minFrequency: filters.minFrequency,
        maxFrequency: filters.maxFrequency,
        minVoltage: filters.minVoltage,
        maxVoltage: filters.maxVoltage,
        minDuration: filters.minDuration,
        maxDuration: filters.maxDuration,
        startTimestamp: filters.startTimestamp,
        stopTimestamp: filters.stopTimestamp,
        visibleIds: map.gridMap.getVisibleIds()});
      ws.websocket.send(json);
    }
  },

  /**
   * Makes a request for details of a particular event.
   * @param rowIndex
   */
  requestDetails: function(rowIndex) {
    if(ws.websocket) {
      var json = JSON.stringify({
        packetType: "public-event-details",
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
      case "public-map-response":
        map.update(data);
        events.update(data);
        break;
      // Update event details
      case "public-event-response":
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
 * Manages the controls/filters.
 */
var filters = {
  /**
   * A mapping from the name of a slider->slider object.
   */
  sliders: {},

  /**
   * Request frequency events from cloud.
   */
  requestFrequency: false,

  /**
   * Request voltage events from cloud.
   */
  requestVoltage: false,

  /**
   * Request heartbeat events from cloud.
   */
  requestHeartbeats: false,

  /**
   * Request events with a frequency >= minFrequency.
   */
  minFrequency: 0.0,

  /**
   * Request events with a frequency <= maxFrequency.
   */
  maxFrequency: 0.0,

  /**
   * Request events with a voltage >= minVoltage.
   */
  minVoltage: 0.0,

  /**
   * Request events with a voltage <= maxVoltage.
   */
  maxVoltage: 0.0,

  /**
   * Request events with a duration >= minDuration.
   */
  minDuration: 0.0,

  /**
   * Request events with a duration <= maxDuration.
   */
  maxDuration: 0.0,

  /**
   * Request severe itic events.
   */
  requestIticSevere: false,

  /**
   * Request moderate itic events.
   */
  requestIticModerate: false,

  /**
   * Request ok itic events.
   */
  requestIticOk: false,

  /**
   * Request events with timestamp >= startTimestamp.
   */
  startTimestamp: 0,

  /**
   * Request events with timestamp <= stopTimestamp.
   */
  stopTimestamp: 0,

  /**
   * Setup action events for controls/filters and initialize the sliders.
   */
  init: function() {
    // Setup date time pickers
    $('#startTimestamp').datetimepicker();
    $('#stopTimestamp').datetimepicker();

    // Action listeners
    $('#updateBtn').click(function() {
      filters.updatePage();
    });

    $('#resetBtn').click(function() {
      filters.reset();
    });

    // Setup sliders
    filters.initSliders();
  },

  /**
   * Initialize sliders.
   */
  initSliders: function() {
    var sliderList = [{title: 'frequency-slider', unit: 'Hz'},
                      {title: 'voltage-slider',   unit: 'V'},
                      {title: 'duration-slider',  unit: 's'}];

    sliderList.map(function(slider) {
      filters.sliders[slider.title] = $('#' + slider.title);
      filters.sliders[slider.title].slider({formater: function(v) {
        return " " + v + " " + slider.unit;
      }});
    });
  },

  /**
   * Returns the min and max range of the passed in slider.
   * @param slider The slider to determine the min/max range of.
   * @returns {*} The min max range in the form of [min, max].
   */
  sliderVals: function(slider) {
    return slider.slider('getAttribute', ['value']);
  },

  /**
   * Returns the minimum of the range given by slider.
   * @param slider The slider to find the minimum range for.
   * @returns {*} The minimum of the range of the passed in slider.
   */
  sliderMin: function(slider) {
    return filters.sliderVals(slider)[0];
  },

  /**
   * Returns the maximum of the range given by slider.
   * @param slider The slider to find the maximum range for.
   * @returns {*} The maximum of the range of the passed in slider.
   */
  sliderMax: function(slider) {
    return filters.sliderVals(slider)[1];
  },

  /**
   * Retrieves the timestamps from the time interval calendar widgets.
   * If the calendars are invalid (i.e. start value after stop value), this method will return an empty
   * list to signify a validation error.
   *
   * If the start calendar is empty, choose "0" for the start of time.
   * If the stop calendar is empty, choose the current time for the stop time.
   * @returns {*} The start and stop timestamp in the form [start, stop] or [] on a validation error.
   */
  timestamps: function() {
    var startTime = $("#startTimestampInput").val() == "" ?
      0 :
      $("#startTimestamp").data("DateTimePicker").getDate()._d.getTime();

    var stopTime = $("#stopTimestampInput").val() == "" ?
      new Date().getTime() :
      $("#stopTimestamp").data("DateTimePicker").getDate()._d.getTime();

    if(startTime < stopTime) {
      return [startTime, stopTime];
    }
    else {
      return [];
    }
  },

  /**
   * Bind the data on the page to the internal data model of the filter.
   * @returns {boolean} true if bound without validation errors, false if there are validation errors.
   */
  update: function() {
    filters.requestFrequency    = $('#requestFrequency').is(':checked');
    filters.requestVoltage      = $('#requestVoltage').is(':checked');
    filters.minFrequency        = filters.sliderMin(filters.sliders['frequency-slider']);
    filters.maxFrequency        = filters.sliderMax(filters.sliders['frequency-slider']);
    filters.minVoltage          = filters.sliderMin(filters.sliders['voltage-slider']);
    filters.maxVoltage          = filters.sliderMax(filters.sliders['voltage-slider']);
    filters.minDuration         = filters.sliderMin(filters.sliders['duration-slider']);
    filters.maxDuration         = filters.sliderMax(filters.sliders['duration-slider']);
    filters.requestIticSevere   = $('#requestIticSevere').is(':checked');
    filters.requestIticModerate = $('#requestIticModerate').is(':checked');
    filters.requestIticOk       = $('#requestIticOk').is(':checked');

    var timestamps = filters.timestamps();
    if(timestamps.length == 2) {
      filters.startTimestamp = timestamps[0];
      filters.stopTimestamp = timestamps[1];
      $("#startTimestamp").removeClass("has-error");
      $("#stopTimestamp").removeClass("has-error");
      $("#error").text(null);
      return true;
    }
    else {
      $("#startTimestamp").addClass("has-error");
      $("#stopTimestamp").addClass("has-error");
      $("#error").text("Start date should be before end date.");
      return false;
    }
  },

  /**
   * Reset all filters/controls back to default state.
   */
  reset: function() {
    // Reset sliders to min/max of slider range
    Object.keys(filters.sliders).map(function(key) {
      var slider = filters.sliders[key];
      var min = slider.slider('getAttribute', ['min']);
      var max = slider.slider('getAttribute', ['max']);
      slider.slider('setValue', [min, max]);
    });

    // Reset timestamps
    $("#startTimestampInput").val(null);
    $("#stopTimestampInput").val(null);

    // Reset check boxes
    $('#requestFrequency').prop('checked', true);
    $('#requestVoltage').prop('checked', true);
    $('#requestIticServere').prop('checked', true);
    $('#requestIticModerate').prop('checked', true);
    $('#requestIticOk').prop('checked', true);

    // Reset map
    gridMap.setView(gridMap.island.OAHU.latLng, gridMap.island.OAHU.defaultZoom);

    // Get new data
    ws.requestUpdate();
  },

  /**
   * Update the page if there are no validation errors.
   */
  updatePage: function() {
    if(filters.update()) {
      ws.requestUpdate();
    }
  }
};

/**
 * Manages the map.
 * @type {{gridMap: (grid|*), init: init, update: update}}
 */
var map = {
  /**
   * Reference to grid object.
   */
  gridMap: grid,

  /**
   * Setup action handlers and set the initial view of the map.
   */
  init: function() {
    map.gridMap.callbacks.onMapChange = function () {
      ws.requestUpdate();
    };

    map.gridMap.initMap("public-map", map.gridMap.island.OAHU.latLng, map.gridMap.island.OAHU.defaultZoom);
  },

  /**
   * Updates the map with new metrics.
   * @param data
   */
  update: function(data) {
    var gridIdToEventMetrics = data['gridIdToEventMetrics'];
    function getMetrics(metrics) {
      return {severe: metrics[0], moderate: metrics[1], ok: metrics[2]};
    }
    var m;
    for (var gridId in gridIdToEventMetrics) {
      if(gridIdToEventMetrics.hasOwnProperty(gridId)) {
        m = getMetrics(gridIdToEventMetrics[gridId]);
        map.gridMap.addEventNumbers(gridId, m.severe, m.moderate, m.ok);
      }
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
    var maxDuration = -1;
    var maxVoltage = -1;
    var minVoltage = Number.MAX_VALUE;
    var maxFrequency = -1;
    var minFrequency = Number.MAX_VALUE;
    var duration, frequency, voltage;
    for (var event in data.events) {
      if(data.events.hasOwnProperty(event)) {
        events.add(data.events[event]);
        duration = parseInt(data.events[event]["duration"]);
        frequency = parseInt(data.events[event]["frequency"]);
        voltage = parseInt(data.events[event]["voltage"]);
        if(duration > maxDuration) {
          maxDuration = duration;
        }
        if(voltage < minVoltage) {
          minVoltage = voltage;
        }
        if(voltage > maxVoltage) {
          maxVoltage = voltage;
        }
        if(frequency < minFrequency) {
          minFrequency = frequency;
        }
        if(frequency > maxFrequency) {
          maxFrequency = frequency;
        }
        // Row index in HTML tables start at 1 instead of 0.
        events.tickerToKey[parseInt(event) + 1] = data.events[event].id;
      }
    }

    // Update duration in filters
    $("#max-duration").text(maxDuration);
    $("#duration-slider").slider("setAttribute", "max", [parseFloat(maxDuration)]);

    $("#max-frequency").text(maxFrequency);
    $("#min-frequency").text(minFrequency);
    $("#frequency-slider").slider("setAttribute", "min", [parseFloat(minFrequency)]);
    $("#frequency-slider").slider("setAttribute", "max", [parseFloat(maxFrequency)]);

    $("#max-voltage").text(maxVoltage);
    $("#min-voltage").text(minVoltage);
    $("#voltage-slider").slider("setAttribute", "min", [parseFloat(minVoltage)]);
    $("#voltage-slider").slider("setAttribute", "max", [parseFloat(maxVoltage)]);

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
    /**
     * Pans and zooms the grid to clicked location.
     */
    $("#details-grid-id").click(function() {
      var centerLat = parseFloat($("#details-center-lat").val());
      var centerLng = parseFloat($("#details-center-lng").val());
      var zoom = parseInt($("#details-zoom").val());
      map.gridMap.setView(L.latLng(centerLat, centerLng), zoom);
    });
  },

  /**
   * Display the details for a particular event.
   * @param event
   */
  show: function(event) {
    // Update hidden fields
    $("#details-center-lat").val(event.centerLat);
    $("#details-center-lng").val(event.centerLng);
    $("#details-zoom").val(map.gridMap.getZoomByDistance(event.gridScale));

    // Update table
    var iticRegion = itic.itic.getRegionOfPoint(event.duration * 1000, itic.itic.voltageToPercentNominalVoltage(event.voltage));
    $("#event-title").text("Event Details - " + event.timestamp + " (" + event.eventType + ")");
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
    $.plot($("#waveform"), points, plotOptions);
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
 * Entry point into the script.
 */
$(document).ready(function () {
  map.init();
  details.init();
  ws.init("ws://emilia.ics.hawaii.edu:8194/public");
  //ws.init("ws://emilia.ics.hawaii.edu:8194/public");
  filters.init();
});
