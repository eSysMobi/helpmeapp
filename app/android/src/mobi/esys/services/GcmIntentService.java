package mobi.esys.services;

import mobi.esys.constants.HMAConsts;
import mobi.esys.helpmeapp.NOActivity;
import mobi.esys.helpmeapp.R;
import mobi.esys.recivers.GcmBroadcastReceiver;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	private NotificationManager notificationManager;
	public static final int NOTIFICATION_ID = HMAConsts.GCM_NOTIFICATION_ID;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		GcmBroadcastReceiver.completeWakefulIntent(intent);
		final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		final String messageType = gcm.getMessageType(intent);

		if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
			sendNotification();
		}

		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification() {
		notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.cancel(NOTIFICATION_ID);

		final PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, NOActivity.class), 0);

		final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getString(R.string.app_name))
				.setStyle(
						new NotificationCompat.BigTextStyle()
								.bigText(getString(R.string.warning)));

		mBuilder.setContentIntent(contentIntent);
		final Notification notification = mBuilder.build();

		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.FLAG_SHOW_LIGHTS;

		notificationManager.notify(NOTIFICATION_ID, notification);
	}
}
