package mobi.esys.helpmeapp;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import mobi.esys.constants.HMAConsts;
import mobi.esys.data_types.AuthData;
import mobi.esys.tasks.FBTokenTask;
import mobi.esys.tasks.GetFBUserIDTask;
import mobi.esys.tasks.RegExpireTask;
import mobi.esys.tasks.RegTask;
import mobi.esys.tasks.VKGetUSerIDTask;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class WebLoginActivity extends Activity {
	private transient WebView loginWebView;
	private transient GoogleCloudMessaging gcm;
	private transient SharedPreferences prefs;
	private transient String provider;
	private transient DateTime date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.android_vk_login);

		date = new DateTime();
		if (getIntent().getExtras() != null) {
			provider = getIntent().getExtras().getString("provider");
		}

		gcm = GoogleCloudMessaging.getInstance(WebLoginActivity.this);

		prefs = getSharedPreferences(HMAConsts.HMA_PREF, Context.MODE_PRIVATE);

		loginWebView = (WebView) findViewById(R.id.loginWebView);

		if (provider.equals("vk")) {
			loginWebView.loadUrl(HMAConsts.VK_LOGIN_URL);
		} else if (provider.equals("fb")) {
			loginWebView.loadUrl(HMAConsts.FB_LOGIN_URL);
		} else {

		}

		loginWebView.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) {
				Log.d("url", url);
				if (provider.equals("vk")) {
					vkAuth(url);
				} else if (provider.equals("fb")) {
					fbAuth(url);
				} else {

				}
			}

		});
	}

	private void vkAuth(String url) {
		String accesToken = "";
		if (url.contains("access_token")) {
			accesToken = url.substring(url.indexOf("code=") + 1,
					url.indexOf("&"));
			if (!accesToken.equals("")) {

				VKGetUSerIDTask getUSerIDTask = new VKGetUSerIDTask(
						WebLoginActivity.this);
				getUSerIDTask.execute(accesToken);
				try {
					AuthData authData = new AuthData("vkontakte",
							getUSerIDTask.get(), accesToken);
					RegTask regTask = new RegTask(WebLoginActivity.this);
					regTask.execute(authData);
				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
				}

			}

		}
	}

	private void fbAuth(String url) {
		String code = "";
		if (!url.contains("graph") && url.contains("code")
				&& !url.contains("error_code")) {
			code = url.substring(url.indexOf("=") + 1, url.indexOf("#"));
			if (!code.equals("")) {
				FBTokenTask fbTokenTask = new FBTokenTask(WebLoginActivity.this);
				fbTokenTask.execute(code);
				String result = "";
				try {

					result = fbTokenTask.get();
					Log.d("fbResult", result);
					int expLength = "&expires=".length();
					int aTokenLength = "access_token=".length();
					Log.d("time", date.toString());
					String expires = result
							.substring(result.indexOf("&expires=") + expLength,
									result.length()).replace("null", "")
							.replace(" ", "").replace("\n", "");
					String aT = result.substring(
							result.indexOf("access_token=") + aTokenLength,
							result.indexOf("&expires=")).replace("null", "");

					Long millis = date.getMillis() + Long.parseLong(expires);
					Log.d("expireMillis", String.valueOf(millis));
					DateTime expireDate = new DateTime(Long.parseLong(expires));
					Log.d("expires", expires);
					Log.d("expires", String.valueOf(TimeUnit.MICROSECONDS
							.toHours(Long.parseLong(expires))));
					DateTimeFormatter timeFormat = DateTimeFormat
							.forPattern("ddMMyyyykkmm");
					Log.d("expires date", timeFormat.print(expireDate));
					Log.d("at", aT);

					GetFBUserIDTask fbUserIDTask = new GetFBUserIDTask(
							WebLoginActivity.this);
					fbUserIDTask.execute(aT);

					AuthData authData = new AuthData("facebook",
							fbUserIDTask.get(), aT,
							timeFormat.print(expireDate));

					RegExpireTask expireTask = new RegExpireTask(
							WebLoginActivity.this);
					expireTask.execute(authData);

				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
				}
			}
			Log.d("fb code", code);
		}
	}

}
