package mobi.esys.recivers;

import mobi.esys.services.SendDataService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SendServerReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent inService = new Intent(context, SendDataService.class);
		context.startService(inService);
	}

}
