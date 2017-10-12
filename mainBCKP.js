var page = require('webpage').create(),
    system = require('system'),
    fs = require('fs'),
    zip = new require('node-zip')(),
    _ = require('lodash-node');

var renderColor = 'yellow';
var zoom = 15,
    size = 1000,
    east, west, north, south, bounds;
    
if (system.args.length != 3) {
  console.warn("Must provide lat,lon and output directory arguments");
  phantom.exit();
}

var outputFile = system.args[system.args.length - 1],
    center = system.args[system.args.length - 2];

try {
  fs.write(outputFile, '');
}
catch (e) {
  console.warn('Cannot write to file ' + outputFile);
  phantom.exit();
}

if (center.split(',').length != 2) {
  console.warn('Must supply lat,lon in the form of lat,lon');
  phantom.exit();
}
else {
  var latlon = center.split(','),
      lon = latlon[0],
      lat = latlon[1];
}

var kmlTemplate = _.template(fs.read('kml.template', 'utf8'));

page.onConsoleMessage = function(msg) {
  console.log(msg);
}

page.viewportSize = { width: size, height: size };
page.open('http://labs.strava.com/heatmap/#'+zoom+'/' + lon + '/' + lat + '/'+renderColor+'/bike',
function() {
  bounds = page.evaluate(
    function() {
    $('#header, #controls, #sidebar').remove();

    // Force redraw and recalculation of map bounds
    $(window).trigger('resize');
    map.invalidateSize();

    var b = map.getBounds();
    return {east: b.getEast(),
            west: b.getWest(),
            north: b.getNorth(),
            south: b.getSouth()};
  });

  var suffix = '/home/radim/stravaGHMdata/png/'+lon+'_'+lat+'_';
  var imageFileName = suffix +'StravaHeatMap_'+renderColor+'.png';

  page.render(imageFileName, {quality: 100});
  var kml = kmlTemplate(_.merge(bounds, {lat: lat, lon: lon}));
  zip.file(imageFileName, fs.read(imageFileName, {mode: 'rb'}), {binary: true});
  zip.file('doc.kml', kml);
  fs.write(outputFile, zip.generate({type: 'string', compression: 'DEFLATE'}), 'wb');


  console.log(typeof bounds);


  //fs.remove(imageFileName);

  phantom.exit();
});



