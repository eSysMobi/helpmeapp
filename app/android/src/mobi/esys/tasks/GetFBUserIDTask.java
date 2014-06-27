package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import android.content.Context;
import android.os.AsyncTask;

public class GetFBUserIDTask extends AsyncTask<String, Void, String> {
	private transient HMAServer hmaServer;

	public GetFBUserIDTask(Context context) {
		this.hmaServer = new HMAServer(context);
	}

	@Override
	protected String doInBackground(String... params) {
		return hmaServer.getFBUserID(params[0]);
	}

}
