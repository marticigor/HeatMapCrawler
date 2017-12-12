package output;

public class Trackpoint {
	private double lon;
	private double lat;

	public Trackpoint(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
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
