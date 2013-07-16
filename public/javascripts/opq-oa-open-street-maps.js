function init() {
    var proj4326 = new OpenLayers.Projection("EPSG:4326");
    var projmerc = new OpenLayers.Projection("EPSG:900913");

    var lonlat = new OpenLayers.LonLat(13.38, 52.52);
    var zoom = 13;
    var mlonlat = new OpenLayers.LonLat(13.3776, 52.5162);

    var map = new OpenLayers.Map("map", {
        controls: [
            new OpenLayers.Control.KeyboardDefaults(),
            new OpenLayers.Control.Navigation(),
            new OpenLayers.Control.LayerSwitcher(),
            new OpenLayers.Control.PanZoomBar(),
            new OpenLayers.Control.MousePosition()
        ],
        maxExtent:
            new OpenLayers.Bounds(-20037508.34,-20037508.34,
                20037508.34, 20037508.34),
        numZoomLevels: 18,
        maxResolution: 156543,
        units: 'm',
        projection: projmerc,
        displayProjection: proj4326
    } );

    var mapnik_layer = new OpenLayers.Layer.OSM.Mapnik("Mapnik");
    map.addLayers([mapnik_layer]);

    lonlat.transform(proj4326, projmerc);
    map.setCenter(lonlat, zoom);

    var size = new OpenLayers.Size(32, 32);
    var offset = new OpenLayers.Pixel(-22, -30);
    var icon = new OpenLayers.Icon('/img/pin-32x32.png', size, offset);
    var marker = new OpenLayers.Marker(mlonlat.transform(proj4326, projmerc), icon);

    var markers = new OpenLayers.Layer.Markers("Markers");
    markers.addMarker(marker);
    map.addLayer(markers);
}