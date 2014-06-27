package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import mobi.esys.data_types.TrackingRecordUnit;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SendRecordToServerTask extends
		AsyncTask<TrackingRecordUnit, Void, Void> {
	private transient HMAServer hmaServer;

	public SendRecordToServerTask(Context context) {
		this.hmaServer = new HMAServer(context);
	}

	@Override
	protected Void doInBackground(TrackingRecordUnit... params) {
		hmaServer.addRecord(params[0]);
		Log.i("datas", params[0].toString());
		return null;
	}

}
