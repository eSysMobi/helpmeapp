package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import mobi.esys.recivers.SendServerReciever;
import mobi.esys.services.SendDataService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class DisableTrackingTask extends AsyncTask<Void, Void, Void> {
	private transient HMAServer hmaServer;
	private transient Context context;

	public DisableTrackingTask(Context context) {
		this.hmaServer = new HMAServer(context);
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		hmaServer.disableTracking();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		context.stopService(new Intent(context, SendDataService.class));
		AlarmManager alarms = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SendServerReciever.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarms.cancel(pendingIntent);
	}
}
