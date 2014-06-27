package mobi.esys.tasks;

import mobi.esys.api.HMAServer;
import android.content.Context;
import android.os.AsyncTask;

public class SendOKTask extends AsyncTask<Void, Void, Void> {
	private transient HMAServer hmaServer;

	public SendOKTask(Context context) {
		this.hmaServer = new HMAServer(context);
	}

	@Override
	protected Void doInBackground(Void... params) {
		hmaServer.sendMessage();
		return null;
	}

}
