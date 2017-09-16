package output;

public class Trackpoint {
 private double lon;
 private double lat;
 public Trackpoint(int x, int y,
  double startLonX,
  double addLonPerPixX,
  double startLatY,
  double addLatPerPixY
 ) {

  //TODO do some Math
  this.lon = x;
  this.lat = y;

 }

 /**
  * 
  */
 public String getLon() {
  return String.valueOf(lon);
 }

 /**
  * 
  */
 public String getLat() {
  return String.valueOf(lat);
 }
}
