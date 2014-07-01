package mobi.esys.tasks;

import mobi.esys.api.HMAServer;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class AddDeviceTask extends AsyncTask<Bundle, Void, JSONObject> {
	private transient HMAServer hmaServer;

	public AddDeviceTask(Context context) {
		this.hmaServer = new HMAServer(context);
	}

	@Override
	protected JSONObject doInBackground(Bundle... params) {
		return hmaServer.addDevice(params[0]);
	}

}
