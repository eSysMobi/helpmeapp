package mobi.esys.tasks;

import mobi.esys.server.HMAServer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class SendDataTask extends AsyncTask<Bundle, Void, Void> {
	private transient HMAServer hmaServer;

	public SendDataTask() {
		this.hmaServer = new HMAServer();
	}

	@Override
	protected Void doInBackground(Bundle... params) {
		hmaServer.sendData(params[0]);
		Log.i("datas",
				params[0].getString("lat") + " " + params[0].getString("lon")
						+ " " + params[0].getString("mvelocity"));
		return null;
	}

}
