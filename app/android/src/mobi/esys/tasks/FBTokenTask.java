package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import android.content.Context;
import android.os.AsyncTask;

public class FBTokenTask extends AsyncTask<String, Void, String> {
	private transient HMAServer server;

	public FBTokenTask(Context context) {
		server = new HMAServer(context);
	}

	@Override
	protected String doInBackground(String... params) {
		return server.getFBToken(params[0]);
	}

}
