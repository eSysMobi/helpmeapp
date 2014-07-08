package mobi.esys.data_types;

import android.location.Location;

public class TrackingRecordUnit {
	private transient String lat;
	private transient String lon;
	private transient String speed;

	public TrackingRecordUnit() {
		super();
	}

	public TrackingRecordUnit(String lat, String lon, String speed) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.speed = speed;
	}

	public TrackingRecordUnit(Location location) {
		super();
		this.lat = String.valueOf(location.getLatitude());
		this.lon = String.valueOf(location.getLongitude());
		this.speed = String.valueOf(3.6 * location.getSpeed());
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	@Override
	public String toString() {
		return "TrackingRecordUnit [getLat()=" + getLat() + ", getLon()="
				+ getLon() + ", getSpeed()=" + getSpeed() + "]";
	};

}
