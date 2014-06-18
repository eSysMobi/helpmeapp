package mobi.esys.services;

import mobi.esys.tasks.SendDataTask;
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
	private transient Bundle datas;
	private transient Criteria criteria;

	public SendDataService() {
		this.datas = new Bundle();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(criteria, true);
		Log.d("location provider", provider);
		locationManager.requestLocationUpdates(provider, 60000, 10, listener);
		Location loc = locationManager.getLastKnownLocation(provider);
		datas.putString("lat", String.valueOf(loc.getLatitude()));
		datas.putString("lon", String.valueOf(loc.getLongitude()));
		datas.putString("mvelocity", String.valueOf(loc.getSpeed()));
		Log.d("data", datas.toString());

		SendDataTask dataTask = new SendDataTask();
		dataTask.execute(datas);
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
			locationManager.requestLocationUpdates(provider, 60000, 10,
					listener);
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onLocationChanged(Location location) {
			datas.putString("lat", String.valueOf(location.getLatitude()));
			datas.putString("lon", String.valueOf(location.getLongitude()));
			datas.putString("mvelocity", String.valueOf(location.getSpeed()));
			Log.d("data", datas.toString());
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
