package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import mobi.esys.data_types.AuthData;
import android.content.Context;
import android.os.AsyncTask;

public class RegExpireTask extends AsyncTask<AuthData, Void, Void> {
	private transient HMAServer server;

	public RegExpireTask(Context context) {
		this.server = new HMAServer(context);
	}

	@Override
	protected Void doInBackground(AuthData... params) {
		server.regWithExpire(params[0]);
		return null;
	}

}
