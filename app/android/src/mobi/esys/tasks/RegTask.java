package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import mobi.esys.data_types.AuthData;
import android.content.Context;
import android.os.AsyncTask;

public class RegTask extends AsyncTask<AuthData, Void, Void> {
	private transient HMAServer server;

	public RegTask(Context context) {
		this.server = new HMAServer(context);
	}

	@Override
	protected Void doInBackground(AuthData... params) {
		server.reg(params[0]);
		return null;
	}

}
