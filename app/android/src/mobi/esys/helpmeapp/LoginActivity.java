package mobi.esys.helpmeapp;

import java.io.IOException;

import mobi.esys.constants.HMAConsts;
import mobi.esys.data_types.AuthData;
import mobi.esys.tasks.AddDeviceTask;
import mobi.esys.tasks.RegExpireTask;
import mobi.esys.tasks.RegTask;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCaptchaDialog;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;

public class LoginActivity extends Activity implements OnClickListener {
	private transient ImageView vkLogBtn;
	private transient ImageView fbLogBtn;
	private transient ImageView twLoginBtn;
	private transient SharedPreferences prefs;
	private transient Bundle fbPrefs;

	private static String sTokenKey = "VK_ACCESS_TOKEN";
	private static String[] sMyScope = new String[] { VKScope.FRIENDS,
			VKScope.WALL, VKScope.NOHTTPS };

	// vk id 4396881
	// fb id 579394165512081
	// fb secret dbafbd72db6923f5a75f3507a0deb06e
	// tw consumer JT8xafimiH941LM3xC4umLPdu
	// tw consumer secret 0TKoZNTaV0wnMSoEFGTWf16RpDuwJCOJnWHMlHwDT6MLfTpCmX

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = getSharedPreferences(HMAConsts.HMA_PREF, MODE_PRIVATE);

		fbPrefs = new Bundle();

		String apiKey = prefs.getString(HMAConsts.HMA_PREF_API_KEY, "");
		String userID = prefs.getString(HMAConsts.HMA_PREF_USER_ID, "");
		if (apiKey.equals("") && userID.equals("")) {

			setContentView(R.layout.activity_login);
			vkLogBtn = (ImageView) findViewById(R.id.vkLoginBtn);
			vkLogBtn.setOnClickListener(this);
			fbLogBtn = (ImageView) findViewById(R.id.fbLoginBtn);
			fbLogBtn.setOnClickListener(this);
			twLoginBtn = (ImageView) findViewById(R.id.twLoginBtn);
			twLoginBtn.setOnClickListener(this);
		} else {
			if (prefs.getString("deviceID", "").equals("")) {
				registerInBackground();
			}

			else {
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
			}

		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, Void>() {
			String regid = "";

			@Override
			protected Void doInBackground(Void... params) {
				GoogleCloudMessaging gcm = GoogleCloudMessaging
						.getInstance(LoginActivity.this);
				try {

					// gcm.unregister();
					regid = gcm.register("598373986171");
					Log.d("regid", regid);

				} catch (IOException e) {
				}

				Bundle adBundle = new Bundle();
				adBundle.putString("gcmToken", regid);
				AddDeviceTask addDeviceTask = new AddDeviceTask(
						LoginActivity.this);
				addDeviceTask.execute(adBundle);
				return null;
			}

			protected void onPostExecute(Void result) {
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
			};

		}.execute(null, null, null);

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(LoginActivity.this, WebLoginActivity.class);
		switch (v.getId()) {
		case R.id.vkLoginBtn:
			VKSdk.initialize(sdkListener, "4396881",
					VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
			VKSdk.authorize(sMyScope);
			break;
		case R.id.fbLoginBtn:
			// intent.putExtra("provider", "fb");
			// startActivity(intent);

			Session.openActiveSession(this, true, new Session.StatusCallback() {

				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
					if (session.isOpened()) {
						DateTimeFormatter timeFormat = DateTimeFormat
								.forPattern("ddMMyyyykkmm");
						DateTime dateTime = new DateTime(session
								.getExpirationDate());

						fbPrefs.putString("at", session.getAccessToken());
						fbPrefs.putString("date",
								timeFormat.print(dateTime.plusHours(4)));

						Request.newMeRequest(session,
								new Request.GraphUserCallback() {

									@Override
									public void onCompleted(GraphUser user,
											Response response) {
										if (user != null) {
											AuthData authData = new AuthData(
													"facebook", user.getId(),
													fbPrefs.getString("at"),
													fbPrefs.getString("date"));
											RegExpireTask expireTask = new RegExpireTask(
													LoginActivity.this);
											expireTask.execute(authData);
										}
									}
								}).executeAsync();
					}
				}
			});
			break;
		case R.id.twLoginBtn:
			intent.putExtra("provider", "tw");
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		VKUIHelper.onResume(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VKUIHelper.onDestroy(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		VKUIHelper.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	private VKSdkListener sdkListener = new VKSdkListener() {
		@Override
		public void onCaptchaError(VKError captchaError) {
			new VKCaptchaDialog(captchaError).show();
		}

		@Override
		public void onTokenExpired(VKAccessToken expiredToken) {
			VKSdk.authorize(sMyScope);
		}

		@Override
		public void onAccessDenied(VKError authorizationError) {
			new AlertDialog.Builder(LoginActivity.this).setMessage(
					authorizationError.errorMessage).show();
		}

		@Override
		public void onReceiveNewToken(VKAccessToken newToken) {
			newToken.saveTokenToSharedPreferences(LoginActivity.this, sTokenKey);
			Log.d("token", newToken.accessToken);
		}

		@Override
		public void onAcceptUserToken(VKAccessToken token) {
			Log.d("token", token.accessToken);
			AuthData authData = new AuthData("vkontakte", token.userId,
					token.accessToken);
			RegTask regTask = new RegTask(LoginActivity.this);
			regTask.execute(authData);
		}
	};
}
