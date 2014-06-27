package mobi.esys.helpmeapp;

import mobi.esys.constants.HMAConsts;
import mobi.esys.data_types.TrackingLimitsUnit;
import mobi.esys.tasks.DisableTrackingTask;
import mobi.esys.tasks.EnableTrackingTask;
import mobi.esys.tasks.SetLimitsTask;
import android.app.Activity;
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
	private transient boolean isStoped = true;
	private transient SharedPreferences preferences;

	private static final int NOTIFY_ID = HMAConsts.WORKING_NOTIFICATION_ID;

	// private transient SharedPreferences prefs;

	// private transient AlarmManager alarms;
	// private transient PendingIntent pendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Resources resources = getResources();
		Bundle extras;

		if (getIntent().getExtras() != null) {
			extras = getIntent().getExtras();
			if (extras.getBoolean("isFromNOActivity")) {
				stopSendService();
			}
		}
		preferences = getSharedPreferences(HMAConsts.HMA_PREF, MODE_PRIVATE);
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
		TrackingLimitsUnit limitsUnit = new TrackingLimitsUnit(timeSpinner
				.getSelectedItem().toString(), velSpinner.getSelectedItem()
				.toString());
		SetLimitsTask limitsTask = new SetLimitsTask(MainActivity.this);
		limitsTask.execute(limitsUnit);
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
			if (!isStoped) {
				stopSendService();
			}
		}

	}

	private void startSendService() {
		isStoped = false;
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("isStoped", isStoped);
		editor.commit();
		EnableTrackingTask enableTrackingTask = new EnableTrackingTask(
				MainActivity.this);
		enableTrackingTask.execute();

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
		DisableTrackingTask disableTracking = new DisableTrackingTask(
				MainActivity.this);
		disableTracking.execute();
		isStoped = true;

		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("isStoped", isStoped);
		editor.commit();
	}

}
