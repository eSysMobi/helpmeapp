package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class AddDeviceTask extends AsyncTask<Bundle, Void, Void> {
	private transient HMAServer hmaServer;

	// private transient Context context;

	public AddDeviceTask(Context context) {
		this.hmaServer = new HMAServer(context);
	}

	@Override
	protected Void doInBackground(Bundle... params) {
		hmaServer.addDevice(params[0]);
		return null;
	}

}
