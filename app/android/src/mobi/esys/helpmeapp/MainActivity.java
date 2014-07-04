package mobi.esys.helpmeapp;

import java.util.Locale;

import mobi.esys.constants.HMAConsts;
import mobi.esys.data_types.TrackingLimitsUnit;
import mobi.esys.tasks.DisableTrackingTask;
import mobi.esys.tasks.EnableTrackingTask;
import mobi.esys.tasks.SetLimitsTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

public class MainActivity extends Activity implements
		android.view.View.OnClickListener, OnCheckedChangeListener {
	private transient Spinner velSpinner;
	private transient Spinner timeSpinner;
	private transient Button setButton;
	private transient Button stopButton;
	private transient Button exitButton;
	private transient boolean isStoped = true;
	private transient SharedPreferences preferences;
	private transient RadioButton mlRadio;
	private transient RadioButton kmRadio;
	private transient RadioGroup group;
	private transient Resources resources;

	private static final int NOTIFY_ID = HMAConsts.WORKING_NOTIFICATION_ID;
	private static AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		resources = getResources();
		Bundle extras;

		Locale current = getResources().getConfiguration().locale;

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setCancelable(false);
		builder.setTitle("Ошибка");
		builder.setMessage("Истекла регистрация войдите еще раз");
		builder.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(HMAConsts.HMA_PREF_API_KEY, "");
				editor.putString(HMAConsts.HMA_PREF_USER_ID, "");
				editor.commit();
			}
		});
		dialog = builder.create();

		if (getIntent().getExtras() != null) {
			extras = getIntent().getExtras();
			if (extras.getBoolean("isFromNOActivity")) {
				stopSendService();
			}
		}
		int[] velocities = { 0 };

		group = (RadioGroup) findViewById(R.id.radioGroup1);

		mlRadio = (RadioButton) findViewById(R.id.mlRadio);
		kmRadio = (RadioButton) findViewById(R.id.kmRadio);

		preferences = getSharedPreferences(HMAConsts.HMA_PREF, MODE_PRIVATE);
		if (current.getLanguage().equals("en")) {
			velocities = resources.getIntArray(R.array.velocitiesMl);
			mlRadio.setChecked(true);
			kmRadio.setChecked(false);
		} else {
			velocities = resources.getIntArray(R.array.velocitiesKM);
			mlRadio.setChecked(false);
			kmRadio.setChecked(true);
		}
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

		group.setOnCheckedChangeListener(this);

		setButton.setOnClickListener(this);
		stopButton.setOnClickListener(this);
		exitButton.setOnClickListener(this);

		sendNotif();
	}

	private void saveToPref() {
		long velocity = 0;
		if (mlRadio.isChecked()) {
			velocity = Math.round(Integer.parseInt(velSpinner.getSelectedItem()
					.toString()) * 1.609);
			Log.d("vel", String.valueOf(velocity));
		} else {
			velocity = Long.parseLong(velSpinner.getSelectedItem().toString());
			Log.d("vel", String.valueOf(velocity));
		}
		TrackingLimitsUnit limitsUnit = new TrackingLimitsUnit(timeSpinner
				.getSelectedItem().toString(), String.valueOf(velocity));
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
	protected void onDestroy() {
		super.onDestroy();
		if (dialog != null) {
			dialog.dismiss();
		}
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
			moveTaskToBack(true);

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
		preferences = getSharedPreferences(HMAConsts.HMA_PREF, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("isStoped", isStoped);
		editor.commit();
	}

	public static void expireDialog() {
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		ArrayAdapter<Integer> velAdapter;
		switch (checkedId) {

		case R.id.kmRadio:
			mlRadio.setChecked(false);
			velAdapter = new ArrayAdapter<Integer>(this,
					android.R.layout.simple_spinner_item,
					intToInteger(resources.getIntArray(R.array.velocitiesKM)));
			velAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			velSpinner.setAdapter(velAdapter);
			// velAdapter.notifyAll();
			break;
		case R.id.mlRadio:
			kmRadio.setChecked(false);
			velAdapter = new ArrayAdapter<Integer>(this,
					android.R.layout.simple_spinner_item,
					intToInteger(resources.getIntArray(R.array.velocitiesMl)));
			velAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			velSpinner.setAdapter(velAdapter);
			// velAdapter.notifyAll();
			break;
		}
	}
}
