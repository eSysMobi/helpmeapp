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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

public class MainActivity extends Activity implements
		android.view.View.OnClickListener, OnCheckedChangeListener {
	private transient AbstractWheel velSpinner;
	private transient AbstractWheel timeSpinner;

	private transient Button setButton;

	private transient boolean isStoped = true;

	private transient SharedPreferences preferences;

	private transient RadioButton mlRadio;
	private transient RadioButton kmRadio;
	private transient RadioGroup group;

	private transient Resources resources;

	private transient String metric;

	private transient int[] velocities;
	private transient int[] times;

	private static final int NOTIFY_ID = HMAConsts.WORKING_NOTIFICATION_ID;

	private static AlertDialog dialog;
	private static AlertDialog goDialog;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getStringExtra("action");
			if (action.equals("close")) {
				MainActivity.this.finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		resources = getResources();
		Bundle extras;

		LocalBroadcastManager.getInstance(this).registerReceiver(
				broadcastReceiver, new IntentFilter("MainActivity"));

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

		group = (RadioGroup) findViewById(R.id.radioGroup1);

		mlRadio = (RadioButton) findViewById(R.id.mlRadio);
		kmRadio = (RadioButton) findViewById(R.id.kmRadio);

		preferences = getSharedPreferences(HMAConsts.HMA_PREF, MODE_PRIVATE);
		if (current.getLanguage().equals("en")) {
			velocities = resources.getIntArray(R.array.velocitiesMl);
			mlRadio.setChecked(true);
			kmRadio.setChecked(false);
			metric = resources.getString(R.string.mlRadioText);
		} else {
			velocities = resources.getIntArray(R.array.velocitiesKM);
			mlRadio.setChecked(false);
			kmRadio.setChecked(true);
			metric = resources.getString(R.string.kmRadioText);
		}
		times = resources.getIntArray(R.array.reaction_intervals);

		velSpinner = (AbstractWheel) findViewById(R.id.velSpinner);
		timeSpinner = (AbstractWheel) findViewById(R.id.timeSpinner);
		setButton = (Button) findViewById(R.id.setBtn);

		velSpinner.setCyclic(true);
		timeSpinner.setCyclic(true);

		ArrayWheelAdapter<Integer> velAdapter = new ArrayWheelAdapter<Integer>(
				this, intToInteger(velocities));
		velAdapter.setItemResource(R.layout.picker_items);
		velAdapter.setItemTextResource(R.id.pickerText);

		ArrayWheelAdapter<Integer> reactTimesAdapter = new ArrayWheelAdapter<Integer>(
				this, intToInteger(times));
		reactTimesAdapter.setItemResource(R.layout.picker_items);
		reactTimesAdapter.setItemTextResource(R.id.pickerText);

		velSpinner.setViewAdapter(velAdapter);
		timeSpinner.setViewAdapter(reactTimesAdapter);

		group.setOnCheckedChangeListener(this);

		setButton.setOnClickListener(this);

		sendNotif();
	}

	private void setLimits() {
		long velocity = 0;
		if (mlRadio.isChecked()) {
			velocity = Math
					.round(velocities[velSpinner.getCurrentItem()] * 1.609);
			Log.d("vel", String.valueOf(velocity));
		} else {
			velocity = velocities[velSpinner.getCurrentItem()];
			Log.d("vel", String.valueOf(velocity));
		}
		TrackingLimitsUnit limitsUnit = new TrackingLimitsUnit(
				String.valueOf(times[timeSpinner.getCurrentItem()]),
				String.valueOf(velocity));
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
		if (goDialog != null) {
			goDialog.dismiss();
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

			AlertDialog.Builder goBuilder = new AlertDialog.Builder(
					MainActivity.this);
			goBuilder.setCancelable(false);
			goBuilder.setTitle("Отслеживание");
			goBuilder
					.setMessage("Вы уверены, что хотите запустить отслеживание со следующими параметрами: скорость: "
							+ String.valueOf(velocities[velSpinner
									.getCurrentItem()])
							+ " "
							+ metric
							+ " время: "
							+ String.valueOf(times[timeSpinner.getCurrentItem()])
							+ " сек.");
			goBuilder.setPositiveButton("OK", new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					setLimits();
					startSendService();
					moveTaskToBack(true);
				}
			});

			goBuilder.setNegativeButton("Отмена", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			goDialog = goBuilder.create();
			goDialog.show();

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

	public void stopSendService() {
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
		ArrayWheelAdapter<Integer> velAdapter;
		switch (checkedId) {

		case R.id.kmRadio:
			mlRadio.setChecked(false);
			velocities = resources.getIntArray(R.array.velocitiesKM);
			velAdapter = new ArrayWheelAdapter<Integer>(this,
					intToInteger(velocities));
			velAdapter.setItemResource(R.layout.picker_items);
			velAdapter.setItemTextResource(R.id.pickerText);
			velSpinner.setViewAdapter(velAdapter);
			metric = resources.getString(R.string.kmRadioText);
			break;
		case R.id.mlRadio:
			kmRadio.setChecked(false);
			velocities = resources.getIntArray(R.array.velocitiesMl);
			velAdapter = new ArrayWheelAdapter<Integer>(this,
					intToInteger(velocities));
			velAdapter.setItemResource(R.layout.picker_items);
			velAdapter.setItemTextResource(R.id.pickerText);
			velSpinner.setViewAdapter(velAdapter);
			metric = resources.getString(R.string.mlRadioText);
			break;
		}
	}
}
