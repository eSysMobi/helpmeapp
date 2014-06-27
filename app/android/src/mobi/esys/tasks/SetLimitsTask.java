package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import mobi.esys.data_types.TrackingLimitsUnit;
import android.content.Context;
import android.os.AsyncTask;

public class SetLimitsTask extends AsyncTask<TrackingLimitsUnit, Void, Void> {
	private transient HMAServer hmaServer;

	public SetLimitsTask(Context context) {
		this.hmaServer = new HMAServer(context);
	}

	@Override
	protected Void doInBackground(TrackingLimitsUnit... params) {
		hmaServer.setLimits(params[0]);
		return null;
	}

}
