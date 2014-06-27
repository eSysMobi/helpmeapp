package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import mobi.esys.data_types.AuthData;
import mobi.esys.helpmeapp.MainActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class RegExpireTask extends AsyncTask<AuthData, Void, Void> {
	private transient HMAServer server;
	private transient Context context;

	public RegExpireTask(Context context) {
		this.context = context;
		this.server = new HMAServer(context);
	}

	@Override
	protected Void doInBackground(AuthData... params) {
		server.regWithExpire(params[0]);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		((Activity) context).finish();
		context.startActivity(new Intent(context, MainActivity.class));
	}

}
