/* HTML Utils */
/**
 * Place the body between two td tags.
 * @param body
 * @returns {string}
 */
function td(body) {
  return "<td>" + body + "</td>";
}

/**
 * Place the body between two tr tags.
 * @param body
 * @returns {string}
 */
function tr(body) {
  return "<tr>" + body + "</tr>";
}

/**
 * Reference to gridMap object.
 */
var gridMap;

/**
 * ITIC plot.
 */
var iticPlot;

/**
 * Reference to websocket connection.
 */
var websocket;

/**
 * Store the row number of each event in the ticker and match it with its primary key.
 * @type {{string, integer}}
 */
var tickerToKey = {};

/**
 * Initialize the grid map and request first set of data.
 */
function initGridMap() {
  gridMap = grid;

  gridMap.callbacks.onMapChange = function () {
    wsRequestUpdate();
  };

  gridMap.initMap("public-map", gridMap.island.OAHU.latLng, gridMap.island.OAHU.defaultZoom);
}

/**
 * Updates the detailed waveform of a chosen event and plots it.
 * @param points The points in the waveform.
 * @param min The minimum-y value to show on the waveform.
 * @param max The maximum-y value to show on the waveform.
 */
function updateWaveform(points, min, max) {
  var plotOptions = {
    min: min - 20,
    max: max + 20,
    zoom: {
      interactive: true
    },
    pan: {
      interactive: true
    },
    acisLabels: {
      show: true
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

/**
 * Initialize ITIC plot.
 */
function initIticPlots() {
  itic = iticPlotter;
  /*itic.init("#itic-plot");

  $("<div id='tooltip'></div>").css({
    position: "absolute",
    display: "none",
    border: "1px solid #fdd",
    padding: "2px",
    "background-color": "#fee",
    opacity: 0.80
  }).appendTo("body");

  $("#itic-plot").bind("plothover", function (event, pos, item) {
    if (item) {
      var duration = item.datapoint[0].toFixed(2);
      var percentNominalVoltage = item.datapoint[1].toFixed(2);

      $("#tooltip").html(p.formatTooltip([["Duration (ms)", duration],
        ["Duration (c)", p.msToC(duration).toFixed(2)],
        ["% nominal voltage", percentNominalVoltage],
        ["Volts", p.percentNominalVoltageToVoltage(percentNominalVoltage)],
        ["ITIC Region", item.series.label]]))
        .css({top: item.pageY + 5, left: item.pageX + 5})
        .fadeIn(200);
    }
    else {
      $("#tooltip").hide();
    }
  });
  itic.update();  */
}

/**
 * Ensures that the start timestamp filter comes before the stop timestamp filter.
 * @returns {boolean} True if start time is before stop time, false otherwise.
 */
function verifyDates() {
  var startTime = $("#startTimestampInput").val() == "" ?
    0 :
    $("#startTimestamp").data("DateTimePicker").getDate()._d.getTime();

  var stopTime = $("#stopTimestampInput").val() == "" ?
    new Date().getTime() :
    $("#stopTimestamp").data("DateTimePicker").getDate()._d.getTime();

  return startTime < stopTime;
}

/**
 * Initializes misc. elements of the page and sets up action handlers.
 */
function initPage() {
  // Setup date time pickers
  $('#startTimestamp').datetimepicker();
  $('#stopTimestamp').datetimepicker();

  // Update page using filtered parameters
  $("#update").click(function() {
    if(verifyDates()) {
      $("#startTimestamp").removeClass("has-error");
      $("#stopTimestamp").removeClass("has-error");
      $("#error").text(null);
      wsRequestUpdate();
    }
    else {
      $("#startTimestamp").addClass("has-error");
      $("#stopTimestamp").addClass("has-error");
      $("#error").text("Start date should be before end date.");
    }
  });

  // Reset page
  $("#reset").click(function () {
    $("#startTimestampInput").val(null);
    $("#stopTimestampInput").val(null);
    $('#requestFrequency').prop('checked', true);
    $('#requestVoltage').prop('checked', true);
    gridMap.setView(gridMap.island.OAHU.latLng, gridMap.island.OAHU.defaultZoom);
    wsRequestUpdate();
  });

  // Move to location from event details
  $("#details-grid-id").click(function() {
    var centerLat = parseFloat($("#details-center-lat").val());
    var centerLng = parseFloat($("#details-center-lng").val());
    var zoom = parseInt($("#details-zoom").val());
    $("#myModal").modal("hide");
    gridMap.setView(L.latLng(centerLat, centerLng), zoom);
  });
}

/**
 * Initialize the websocket connection.
 */
function initWebsocket() {
  //websocket = new WebSocket("ws://emilia.ics.hawaii.edu:8194/public", "protocolOne");
  websocket = new WebSocket("ws://localhost:8194/public", "protocolOne");
  websocket.onopen = function(event) {
    wsRequestUpdate();
  };
  websocket.onmessage = wsOnMessage;
}

/**
 * Request new data for the page using the given map area and filtered elements.
 */
function wsRequestUpdate() {
  if (websocket) {
    var startTime = $("#startTimestampInput").val() == "" ?
      0 :
      $("#startTimestamp").data("DateTimePicker").getDate()._d.getTime();

    var stopTime = $("#stopTimestampInput").val() == "" ?
      new Date().getTime() :
      $("#stopTimestamp").data("DateTimePicker").getDate()._d.getTime();

    var json = JSON.stringify({
      packetType: "public-update",
      requestFrequency: $('#requestFrequency').is(':checked'),
      requestVoltage: $('#requestVoltage').is(':checked'),
      requestHeartbeats: false,
      startTimestamp: startTime,
      stopTimestamp: stopTime,
      visibleIds: gridMap.getVisibleIds()});
    websocket.send(json);
  }
}

/**
 * Request the details for a particular event.
 * @param rowIndex The row index where the event is stored.
 */
function wsRequestDetails(rowIndex) {
  if(websocket) {
    var json = JSON.stringify({
      packetType: "public-event-details",
      pk: tickerToKey[rowIndex]
    });
    websocket.send(json);
  }
}

/**
 * Handles packets received from the server.
 * @param event Stores the data sent from the server.
 */
function wsOnMessage(event) {
  var data = JSON.parse(event.data);
  switch(data.packetType) {
    case "public-map-response":
      handleUpdate(data);
      break;
    case "public-event-response":
      showEventDetails(data);
      break;
    default:
      console.log("Unknown packet type: " + data.packetType);
      break;
  }
}

/**
 * General page update received from server. Update with new data.
 * @param data Data for updating the main page.
 */
function handleUpdate(data) {
  gridMap.redrawMap();
  updateStatistics(data);
  updateMap(data);
  updateEvents(data);
}

/**
 * Updates the badge statistics.
 * @param data Data containing the statistics.
 */
function updateStatistics(data) {
  $("#totalRegisteredDevices").text(data.totalRegisteredDevices);
  $("#totalActiveDevices").text(data.totalActiveDevices);
  $("#totalEvents").text(data.totalEvents);
  $("#totalFrequencyEvents").text(data.totalFrequencyEvents);
  $("#totalVoltageEvents").text(data.totalVoltageEvents);
}

/**
 * Updates the grid map with number of devices and number of events.
 * @param data Data to update the grid map with.
 */
function updateMap(data) {
  // Update device numbers
  var devices = data.gridIdsToDevices;
  for (var id in devices) {
    if(devices.hasOwnProperty(id)) {
      gridMap.addNumberOfDevices(id, devices[id]);
    }
  }

  // Update event numbers
  var events = data.gridIdsToEvents;
  for (var idd in events) {
    if(events.hasOwnProperty(idd)) {
      gridMap.addNumberOfEvents(idd, events[idd]);
    }
  }
}

/**
 * Updates the event ticker.
 * @param data Events.
 */
function updateEvents(data) {
  var events = data.events;
  clearTicker();
  for (var event in events) {
    if(events.hasOwnProperty(event)) {
      addEventToTicker(events[event]);
      tickerToKey[parseInt(event) + 1] = events[event].pk;
    }
  }

  // Handle click events on event ticker
  $('#ticker').find('> tbody > tr').click(function() {
    wsRequestDetails(this.rowIndex);
  });
}

/**
 * Update and display modal dialog containing event details.
 * @param event Event details.
 */
function showEventDetails(event) {
  // Update hidden fields
  $("#details-center-lat").val(event.centerLat);
  $("#details-center-lng").val(event.centerLng);
  $("#details-zoom").val(gridMap.getZoomByDistance(event.gridScale));

  // Update table
  $("#details-timestamp").text(event.timestamp);
  $("#details-event-type").text(event.eventType);
  $("#details-frequency").text(event.frequency);
  $("#details-voltage").text(event.voltage);
  $("#details-duration").text(event.duration);
  $("#details-itic-severity").text("N/A");
  $("#details-event-level").text(event.eventLevel);
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
  updateWaveform(points, min, max);

  // Display modal
  $("#myModal").modal("show");
}

/**
 * Add event to bottom of event ticket.
 * @param event Event to add to ticker.
 */
function addEventToTicker(event) {
  var eventValue;
  var iticRegion = itic.getRegionOfPoint(event.duration * 1000, itic.voltageToPercentNominalVoltage(event.voltage));
  var badgeClass;

  switch(iticRegion) {
    case itic.Region.NO_INTERRUPTION:
      badgeClass = "itic-no-interruption";
      break;
    case itic.Region.NO_DAMAGE:
      badgeClass = "itic-no-damage";
      break;
    case Region.PROHIBITED:
      badgeClass = "itic-prohibited";
      break;
  }

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
  var regionColor = "<span class='badge itic-badge " + badgeClass +"'>&nbsp;</span>";
  var row = tr(td(event.timestamp) + td(event.type) + td(event.duration + " s") + td(eventValue) + td(iticRegion +  regionColor));
  $(row).appendTo("#ticker > tbody");
}

/**
 * Clears all events from the ticker.
 */
function clearTicker() {
  $("#ticker").find("> tbody").children().remove();
  while(tickerToKey.length > 0) {
    tickerToKey.pop();
  }
}

/**
 * Initializes the page.
 */
function init() {
  initGridMap();
  initPage();
  initWebsocket();
  initIticPlots();
}

$(document).ready(function () {
  init();
});
