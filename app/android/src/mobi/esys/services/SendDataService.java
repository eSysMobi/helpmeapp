package mobi.esys.services;

import mobi.esys.constants.HMAConsts;
import mobi.esys.data_types.TrackingRecordUnit;
import mobi.esys.tasks.SendRecordToServerTask;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SendDataService extends Service {
	private transient LocationManager locationManager;
	private transient TrackingRecordUnit recordUnit;
	private transient Criteria criteria;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(criteria, true);
		Log.d("location provider", provider);
		locationManager.requestLocationUpdates(provider, HMAConsts.SEND_DELAY,
				HMAConsts.REQUEST_LOCATION_DIST, listener);
		Location loc = locationManager.getLastKnownLocation(provider);
		recordUnit = new TrackingRecordUnit(loc);
		Log.d("data", recordUnit.toString());

		SendRecordToServerTask dataTask = new SendRecordToServerTask(
				getApplicationContext());
		dataTask.execute(recordUnit);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("service", "service stop");
	}

	private LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {
			locationManager.requestLocationUpdates(provider,
					HMAConsts.SEND_DELAY, HMAConsts.REQUEST_LOCATION_DIST,
					listener);
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onLocationChanged(Location location) {
			recordUnit = new TrackingRecordUnit(location);
			Log.d("data", recordUnit.toString());
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
