package mobi.esys.helpmeapp;

import mobi.esys.constants.HMAConsts;
import mobi.esys.recivers.SendServerReciever;
import mobi.esys.services.SendDataService;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends Activity implements OnClickListener {
	private transient Spinner velSpinner;
	private transient Spinner timeSpinner;
	private transient Button setButton;
	private transient Button stopButton;
	private transient Button exitButton;
	private static final int SEND_DELAY = 60000;
	private static final int NOTIFY_ID = 101;
	private transient SharedPreferences prefs;
	private transient AlarmManager alarms;
	private transient PendingIntent pendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Resources resources = getResources();

		prefs = getSharedPreferences(HMAConsts.HMAPref, MODE_PRIVATE);
		int[] velocities = resources.getIntArray(R.array.velocities);
		int[] reaction_times = resources
				.getIntArray(R.array.reaction_intervals);

		velSpinner = (Spinner) findViewById(R.id.velSpinner);
		timeSpinner = (Spinner) findViewById(R.id.timeSpinner);
		setButton = (Button) findViewById(R.id.setBtn);
		stopButton = (Button) findViewById(R.id.stopBtn);
		exitButton = (Button) findViewById(R.id.exitBtn);

		ArrayAdapter<Integer> velAdapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item, intToInteger(velocities));
		velAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<Integer> reactTimesAdapter = new ArrayAdapter<Integer>(
				this, android.R.layout.simple_spinner_item,
				intToInteger(reaction_times));
		reactTimesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		velSpinner.setAdapter(velAdapter);
		timeSpinner.setAdapter(reactTimesAdapter);

		setButton.setOnClickListener(this);
		stopButton.setOnClickListener(this);
		exitButton.setOnClickListener(this);

		sendNotif();
	}

	private void saveToPref() {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("vel",
				Integer.parseInt(velSpinner.getSelectedItem().toString()));
		editor.putInt("actTime",
				Integer.parseInt(timeSpinner.getSelectedItem().toString()));
		editor.commit();
	}

	private Integer[] intToInteger(int[] array) {
		Integer[] resultArray = new Integer[array.length];
		for (int i = 0; i < resultArray.length; i++) {
			resultArray[i] = array[i];
		}
		return resultArray;
	}

	@Override
	protected void onStop() {
		moveTaskToBack(true);
		Log.d("lifecycle", "onStop");
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.setBtn) {
			saveToPref();
			startSendService();
		} else if (v.getId() == R.id.exitBtn) {
			stopSendService();
			NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(NOTIFY_ID);
			finish();
		} else {
			stopSendService();
		}

	}

	private void startSendService() {
		alarms = (AlarmManager) this.getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(MainActivity.this, SendServerReciever.class);

		pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarms.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
				SEND_DELAY, pendingIntent);
		Log.i("start", "start");
	}

	void sendNotif() {
		Intent notificationIntent = new Intent(MainActivity.this,
				MainActivity.class);

		PendingIntent contentIntent = PendingIntent.getActivity(
				MainActivity.this, 0, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Resources res = getResources();

		Notification.Builder builder = new Notification.Builder(
				MainActivity.this);

		builder.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(res.getString(R.string.app_name))
				.setAutoCancel(false)
				.setOngoing(true)
				.setContentTitle(res.getString(R.string.app_name))
				.setContentText(
						res.getString(R.string.app_name) + " status: Work");

		@SuppressWarnings("deprecation")
		Notification n = builder.getNotification();
		nm.notify(NOTIFY_ID, n);
	}

	private void stopSendService() {
		stopService(new Intent(MainActivity.this, SendDataService.class));
		alarms = (AlarmManager) this.getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(MainActivity.this, SendServerReciever.class);

		pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarms.cancel(pendingIntent);
	}

}
