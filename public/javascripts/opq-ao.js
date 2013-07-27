function addDynamicCss (location) {
    var cssRef = document.createElement("link");
    cssRef.setAttribute("rel", "stylesheet");
    cssRef.setAttribute("media", "screen");
    cssRef.setAttribute("href", location);
    document.getElementsByTagName("head")[0].appendChild(cssRef);
}

function addDynamicJs (location) {
    var jsRef = document.createElement("script");
    jsRef.setAttribute("src", location);
    jsRef.setAttribute("type", "text/javascript");
    document.getElementsByTagName("head")[0].appendChild(jsRef);
}