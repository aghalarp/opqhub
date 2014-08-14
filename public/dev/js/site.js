/* HTML Utils */
function td(body) {
  return "<td>" + body + "</td>";
}

function tr(body) {
  return "<tr>" + body + "</tr>";
}

// Grid map
var g;

// Websocket connection
var ws;

// ticker row to event primary key
var tickerToKey = {};

function initGridMap() {
  g = grid;

  g.callbacks.onMapChange = function () {
    wsRequestUpdate();
  };

  g.initMap("public-map", g.island.OAHU.latLng, g.island.OAHU.defaultZoom);
}

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

function initIticPlots() {
  var p = iticPlotter;
  p.init("#itic-plot");

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
  p.update();
}

function verifyDates() {
  var startTime = $("#startTimestampInput").val() == "" ?
    0 :
    $("#startTimestamp").data("DateTimePicker").getDate()._d.getTime();

  var stopTime = $("#stopTimestampInput").val() == "" ?
    new Date().getTime() :
    $("#stopTimestamp").data("DateTimePicker").getDate()._d.getTime();

  return startTime < stopTime;
}

function initPage() {
  // Setup date time pickers
  $('#startTimestamp').datetimepicker();
  $('#stopTimestamp').datetimepicker();

  // Update map with filtered parameters
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
    g.setView(g.island.OAHU.latLng, g.island.OAHU.defaultZoom);
    wsRequestUpdate();
  });

  // Move to location from event details
  $("#details-grid-id").click(function() {
    var centerLat = parseFloat($("#details-center-lat").val());
    var centerLng = parseFloat($("#details-center-lng").val());
    var zoom = parseInt($("#details-zoom").val());
    $("#myModal").modal("hide");
    g.setView(L.latLng(centerLat, centerLng), zoom);
  });
}

function initWebsocket() {
  ws = new WebSocket("ws://128.171.10.188:9000/public", "protocolOne");
  ws.onopen = wsOnOpen;
  ws.onmessage = wsOnMessage;
}

function wsOnOpen(event) {
  wsRequestUpdate();
}

function wsRequestUpdate() {
  if (ws) {
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
      visibleIds: g.getVisibleIds()});
    ws.send(json);
  }
}

function wsRequestDetails(r) {
  if(ws) {
    var json = JSON.stringify({
      packetType: "public-event-details",
      pk: tickerToKey[r]
    });
    ws.send(json);
  }
}

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

function handleUpdate(data) {
  g.redrawMap();
  updateStatistics(data);
  updateMap(data);
  updateEvents(data);
}

function updateStatistics(data) {
  $("#totalRegisteredDevices").text(data.totalRegisteredDevices);
  $("#totalActiveDevices").text(data.totalActiveDevices);
  $("#totalEvents").text(data.totalEvents);
  $("#totalFrequencyEvents").text(data.totalFrequencyEvents);
  $("#totalVoltageEvents").text(data.totalVoltageEvents);
}

function updateMap(data) {
  // Update device numbers
  var devices = data.gridIdsToDevices;
  for (var id in devices) {
    g.addNumberOfDevices(id, devices[id]);
  }

  // Update event numbers
  var events = data.gridIdsToEvents;
  for (var idd in events) {
    g.addNumberOfEvents(idd, events[idd]);
  }
}

function updateEvents(data) {
  var events = data.events;
  clearTicker();
  for (var event in events) {
    addEventToTicker(events[event]);
    tickerToKey[parseInt(event) + 1] = events[event].pk;
  }

  // Handle click events on event ticker
  $("#ticker > tbody > tr").click(function() {
    wsRequestDetails(this.rowIndex);
  });
}

function showEventDetails(event) {
  // Update hidden fields
  $("#details-center-lat").val(event.centerLat);
  $("#details-center-lng").val(event.centerLng);
  $("#details-zoom").val(g.getZoomByDistance(event.gridScale));

  // Update table
  $("#details-timestamp").text(event.timestamp);
  $("#details-event-type").text(event.eventType);
  $("#details-frequency").text(event.frequency);
  $("#details-voltage").text(event.voltage);
  $("#details-duration").text("N/A");
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

function addEventToTicker(event) {
  var row = tr(td(event.timestamp) + td(event.type) + td(event.itic));
  $(row).prependTo("#ticker > tbody");
}

function clearTicker() {
  $("#ticker > tbody").children().remove();
  while(tickerToKey.length > 0) {
    tickerToKey.pop();
  }
}

function init() {
  initGridMap();
  initPage();
  initWebsocket();
}

$(document).ready(function () {
  init();
});