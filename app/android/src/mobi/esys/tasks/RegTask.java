package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import mobi.esys.data_types.AuthData;
import mobi.esys.helpmeapp.MainActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class RegTask extends AsyncTask<AuthData, Void, Void> {
	private transient HMAServer server;
	private transient Context context;

	public RegTask(Context context) {
		this.server = new HMAServer(context);
		this.context = context;
	}

	@Override
	protected Void doInBackground(AuthData... params) {
		server.reg(params[0]);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		((Activity) context).finish();
		context.startActivity(new Intent(context, MainActivity.class));
	}
}
