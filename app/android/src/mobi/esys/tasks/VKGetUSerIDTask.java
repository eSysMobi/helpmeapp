package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import android.content.Context;
import android.os.AsyncTask;

public class VKGetUSerIDTask extends AsyncTask<String, Void, String> {
	private transient HMAServer hmaServer;

	// private transient Context context;

	public VKGetUSerIDTask(Context context) {
		// this.context = context;
		this.hmaServer = new HMAServer(context);
	}

	@Override
	protected String doInBackground(String... params) {
		return hmaServer.getVKUser(params[0]);
	}

}
