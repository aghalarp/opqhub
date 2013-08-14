var map;

var osmUrl = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
var osmAttrib = 'Map data © OpenStreetMap contributors';
var osm = new L.TileLayer(osmUrl, { attribution: osmAttrib });
var boundingBoxLayer;
var geoJsonLayer;
var iconLayer;

var deviceMarker = L.icon({iconUrl: '/assets/images/opq-icon.png'});
var frequencyMarker = L.icon({iconUrl: '/assets/images/frequency-alert-icon.png'});
var voltageMarker = L.icon({iconUrl: '/assets/images/voltage-alert-icon.png'});

var stateId;
var cityId;
var zipId;
var streetNameId;
var streetNumberId;
var latitudeId;
var longitudeId;

var dataIndex = 0;

function initMap(mapId, state, city, zip, streetName, streetNumber, latitude, longitude) {
  stateId = state;
  cityId = city;
  zipId = zip;
  streetNameId = streetName;
  streetNumberId = streetNumber;
  latitudeId = latitude;
  longitudeId = longitude;

  map = L.map(mapId);
  map.addLayer(osm);
  boundingBoxLayer = new L.layerGroup().addTo(map);
  geoJsonLayer = L.geoJson().addTo(map);
  iconLayer = new L.layerGroup().addTo(map);
  map.setView([ 21.4667, -157.9833 ], 10);
}

function getMapInfo() {
  var state = $("#" + stateId).val();
  var city = $("#" + cityId).val();
  var zip = $("#" + zipId).val();
  var streetName = $("#" + streetNameId).val();
  var streetNumber = $("#" + streetNumberId).val();
  var query = streetNumber + " " + streetName + " " + city + " " + state + " " + zip;
  var nominatimApi = "http://nominatim.openstreetmap.org/search/";

  var mapData;

  $.ajax({
    type: "GET",
    url: nominatimApi,
    async: false,
    data: {
      q: query,
      countrycode: "us",
      format: "json",
      polygon_geojson: 1,
      email: "achriste@hawaii.edu"
    }
  }).done(function(data){
      mapData = data;
    });

  return mapData;
}

function updateLatLong(data) {
  var lat = data[dataIndex].lat;
  var lon = data[dataIndex].lon;

  $("#" + latitudeId).val(lat);
  $("#" + longitudeId).val(lon);
  return [lat, lon];
}

function placeDeviceMarker(latitude, longitude) {
  L.marker([latitude, longitude], {icon: deviceMarker}).addTo(iconLayer)
    .bindPopup("Latitude: " + latitude + "<br />" + "Longitude: " + longitude);
}

function makeBoundingBox(data) {
  var boundingBoxCoords = data[ dataIndex ].boundingbox;
  var southWest = new L.LatLng(boundingBoxCoords[0], boundingBoxCoords[2]);
  var northEast = new L.LatLng(boundingBoxCoords[1], boundingBoxCoords[3]);
  var bounds = new L.LatLngBounds(southWest, northEast);
  var boundingBox = L.polygon([
    bounds.getNorthWest(),
    bounds.getNorthEast(),
    bounds.getSouthEast(),
    bounds.getSouthWest()
  ], {color: 'red'}).addTo(boundingBoxLayer);
  return boundingBox;
}

function makeJsonPolygon(data) {
  var polygon = data[dataIndex].geojson;
  geoJsonLayer.addData(polygon);
  return polygon;
}

function panTo(latitude, longitude, zoom) {
  map.panTo([latitude, longitude], zoom);
}

function previewMap() {
  clearMap();
  var data = getMapInfo();
  var latLon = updateLatLong(data);
  placeDeviceMarker(latLon[0], latLon[1]);
  makeBoundingBox(data);
  makeJsonPolygon(data);
  panTo(latLon[0], latLon[1], 10);
}

function clearMap() {
  map.removeLayer(geoJsonLayer);
  map.removeLayer(boundingBoxLayer);
  map.removeLayer(iconLayer);
  geoJsonLayer = L.geoJson().addTo(map);
  boundingBoxLayer = new L.layerGroup().addTo(map);
  iconLayer = new L.layerGroup().addTo(map);
}