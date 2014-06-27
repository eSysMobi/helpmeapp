package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import mobi.esys.constants.HMAConsts;
import mobi.esys.recivers.SendServerReciever;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class EnableTrackingTask extends AsyncTask<Void, Void, Void> {
	private transient HMAServer hmaServer;
	private transient Context context;

	public EnableTrackingTask(Context context) {
		this.context = context;
		this.hmaServer = new HMAServer(context);
	}

	@Override
	protected Void doInBackground(Void... params) {
		hmaServer.enableTracking();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		AlarmManager alarms = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SendServerReciever.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarms.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
				HMAConsts.SEND_DELAY, pendingIntent);
		Log.i("start", "start");
	}

}
