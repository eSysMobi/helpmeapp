package mobi.esys.api;

import java.io.IOException;

import mobi.esys.constants.HMAConsts;
import mobi.esys.data_types.AuthData;
import mobi.esys.data_types.TrackingLimitsUnit;
import mobi.esys.data_types.TrackingRecordUnit;
import mobi.esys.helpmeapp.LoginActivity;
import mobi.esys.helpmeapp.MainActivity;
import mobi.esys.helpmeapp.R;
import mobi.esys.net.APIRequest;
import mobi.esys.tasks.AddDeviceTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class HMAServer {
	private transient Context context;
	private transient APIRequest apirequest;
	private transient SharedPreferences preferences;
	private transient String userID;
	private transient String apiKey;
	private static final String JSON_ERROR_TAG = "jsonExp";
	private static final String API_ERROR_TAG = "apiExp";
	private static final String SERVER_INFO_TAG = "apiInfo";
	private static final String API_USER = HMAConsts.API_USER_PREFIX;
	private static final String API_DEVICE = HMAConsts.API_DEVICE_PREFIX;
	private static final String PARAM_TOKEN = "&token=";
	private transient Resources resources;

	public HMAServer(Context context) {
		this.context = context;
		this.resources = context.getResources();
		this.apirequest = new APIRequest();
		this.preferences = context.getSharedPreferences(HMAConsts.HMA_PREF,
				Context.MODE_PRIVATE);
		this.userID = preferences.getString(HMAConsts.HMA_PREF_USER_ID, "");
		this.apiKey = preferences.getString(HMAConsts.HMA_PREF_API_KEY, "");

	}

	public void reg(AuthData authData) {
		try {
			StringBuilder reqRequestURL = new StringBuilder();
			reqRequestURL.append(API_USER)
					.append(HMAConsts.API_USER_LOGIN_REG_UPDATE)
					.append("network=").append(authData.getProvider())
					.append(PARAM_TOKEN).append(authData.getAccessToken())
					.append("&id=").append(authData.getSocID());
			JSONObject regJSONObject = apirequest
					.doJSONGetRequesOKHTTP(reqRequestURL.toString());
			if (regJSONObject.getString("status").equals("success")) {

				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(HMAConsts.HMA_PREF_API_KEY,
						regJSONObject.getString("apikey"));
				editor.putString(HMAConsts.HMA_PREF_USER_ID,
						regJSONObject.getString("id"));
				editor.putString("deviceID", "");
				editor.commit();

				if (preferences.getString("deviceID", "").equals("")) {
					registerInBackground();
				}
			} else {
			}
		} catch (JSONException e) {
			Log.d(JSON_ERROR_TAG, resources.getString(R.string.regError));
		}

	}

	public void regWithExpire(AuthData authData) {
		try {
			StringBuilder reqRequestURL = new StringBuilder();
			reqRequestURL.append(API_USER)
					.append(HMAConsts.API_USER_LOGIN_REG_UPDATE)
					.append("network=").append(authData.getProvider())
					.append(PARAM_TOKEN).append(authData.getAccessToken())
					.append("&id=").append(authData.getSocID()).append("&exp=")
					.append(authData.getExpireTime());
			JSONObject regJSONObject = apirequest
					.doJSONGetRequesOKHTTP(reqRequestURL.toString());
			if (regJSONObject.getString("status").equals("success")) {

				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(HMAConsts.HMA_PREF_API_KEY,
						regJSONObject.getString("apikey"));
				editor.putString(HMAConsts.HMA_PREF_USER_ID,
						regJSONObject.getString("id"));
				editor.putString("deviceID", "");
				editor.commit();

				if (preferences.getString("deviceID", "").equals("")) {
					registerInBackground();
				}
			} else {
			}
		} catch (JSONException e) {
			Log.d(JSON_ERROR_TAG, resources.getString(R.string.regError));
		}

	}

	public JSONObject addDevice(Bundle addDeviceBundle) {
		JSONObject addDeviceJSONObject = new JSONObject();
		try {
			StringBuilder addDeviceRequestURL = new StringBuilder();
			addDeviceRequestURL.append(API_DEVICE)
					.append(HMAConsts.API_DEVICE_ADD_DEVICE).append("id=")
					.append(userID).append("&apikey=").append(apiKey)
					.append(PARAM_TOKEN)
					.append(addDeviceBundle.getString("gcmToken"))
					.append("&type=android");

			addDeviceJSONObject = apirequest
					.doJSONGetRequesOKHTTP(addDeviceRequestURL.toString());

			if (addDeviceJSONObject.getString("status").equals("success")) {
				SharedPreferences.Editor editor = preferences.edit();
				if (addDeviceJSONObject.has("deviceId")
						&& !addDeviceJSONObject.getString("deviceId")
								.equals(""))
					editor.putString("deviceID",
							addDeviceJSONObject.getString("deviceId"));
				editor.commit();
				context.startActivity(new Intent(context, MainActivity.class));

			} else if (addDeviceJSONObject.has("error")
					&& addDeviceJSONObject.getString("error").equals(
							"Expired_token")) {
				if (((Activity) context) instanceof LoginActivity) {
					((LoginActivity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							LoginActivity.expireDialog();
						}
					});

				}
			}
		} catch (JSONException e) {
			Log.d(JSON_ERROR_TAG, resources.getString(R.string.deviceRegError));
		}

		return addDeviceJSONObject;
	}

	public void setLimits(TrackingLimitsUnit limitsUnit) {
		String deviceID = preferences.getString("deviceID", "");
		try {
			StringBuilder setLimitsrequestURL = new StringBuilder();
			setLimitsrequestURL.append(API_USER)
					.append(HMAConsts.API_USER_SET_LIMITS).append("deviceId=")
					.append(deviceID).append("&apikey=").append(apiKey)
					.append("&speedlimit=").append(limitsUnit.getSpeedLimit())
					.append("&timelimit=").append(limitsUnit.getTimeLimit());

			JSONObject setLimitsJSONObject = apirequest
					.doJSONGetRequesOKHTTP(setLimitsrequestURL.toString());
			if (setLimitsJSONObject.getString("status").equals("success")) {
				// Toast.makeText(context,
				// resources.getString(R.string.limitSuccess),
				// toastDuration).show();

			} else if (setLimitsJSONObject.has("error")
					&& setLimitsJSONObject.getString("error").equals(
							"Expired_token")) {
				if (((Activity) context) instanceof MainActivity) {
					((MainActivity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							MainActivity.expireDialog();
						}
					});

				}
			}

		} catch (JSONException e) {
			Log.d(JSON_ERROR_TAG, resources.getString(R.string.limitsError));
		}
	}

	public void addRecord(TrackingRecordUnit recordUnit) {
		String deviceID = preferences.getString("deviceID", "");
		try {
			StringBuilder addRecURL = new StringBuilder();
			addRecURL.append(API_USER).append(HMAConsts.API_USER_ADD_RECORD)
					.append("deviceId=").append(deviceID).append("&apikey=")
					.append(apiKey).append("&lat=").append(recordUnit.getLat())
					.append("&lng=").append(recordUnit.getLon())
					.append("&speed=").append(recordUnit.getSpeed());
			JSONObject addRecJSONObject = apirequest
					.doJSONGetRequesOKHTTP(addRecURL.toString());

			if (addRecJSONObject.getString("status").equals("fail")) {
				Log.d(API_ERROR_TAG, addRecJSONObject.getString("error"));
			}
		} catch (JSONException exception) {
			Log.d(JSON_ERROR_TAG, resources.getString(R.string.addRecordError));
		}
	}

	public void disableTracking() {
		String deviceID = preferences.getString("deviceID", "");
		try {
			StringBuilder disableTrackingURL = new StringBuilder();
			disableTrackingURL.append(API_DEVICE)
					.append(HMAConsts.API_DEVICE_DISABLE_TRACKING)
					.append("deviceId=").append(deviceID).append("&apikey=")
					.append(apiKey);
			JSONObject disableTrackingJSONObject = apirequest
					.doJSONGetRequesOKHTTP(disableTrackingURL.toString());
			if (disableTrackingJSONObject.getString("status").equals("success")) {
				Log.i(SERVER_INFO_TAG, "disable server tracking");
			} else if (disableTrackingJSONObject.getString("status").equals(
					"fail")) {
				Log.d(API_ERROR_TAG,
						disableTrackingJSONObject.getString("error"));
			}
		} catch (JSONException exception) {
			Log.d(JSON_ERROR_TAG, "Can't disable server tracking");
		}
	}

	public void enableTracking() {
		String deviceID = preferences.getString("deviceID", "");
		try {
			StringBuilder enableTrackingURL = new StringBuilder();
			enableTrackingURL.append(API_DEVICE)
					.append(HMAConsts.API_DEVICE_ENABLE_TRACKING)
					.append("deviceId=").append(deviceID).append("&apikey=")
					.append(apiKey);
			JSONObject enableTrackingJSONObject = apirequest
					.doJSONGetRequesOKHTTP(enableTrackingURL.toString());
			if (enableTrackingJSONObject.getString("status").equals("success")) {
				Log.i(SERVER_INFO_TAG, "enable server tracking");
			} else if (enableTrackingJSONObject.getString("status").equals(
					"fail")) {
				Log.d(API_ERROR_TAG,
						enableTrackingJSONObject.getString("error"));
			} else if (enableTrackingJSONObject.has("error")
					&& enableTrackingJSONObject.getString("error").equals(
							"Expired_token")) {
				if (((Activity) context) instanceof MainActivity) {
					MainActivity.expireDialog();
				}
			}
		} catch (JSONException exception) {
			Log.d(JSON_ERROR_TAG, "Can't enable server tracking");
		}
	}

	public void sendMessage() {
		String deviceID = preferences.getString("deviceID", "");
		try {
			StringBuilder sendMsgURL = new StringBuilder();
			sendMsgURL.append(API_USER).append(HMAConsts.API_USER_SENDMESSAGE)
					.append("deviceId=").append(deviceID).append("&apikey=")
					.append(apiKey);
			JSONObject sendMsgJSONObject = apirequest
					.doJSONGetRequesOKHTTP(sendMsgURL.toString());
			if (sendMsgJSONObject.getString("status").equals("success")) {
				Log.i(SERVER_INFO_TAG, "send message to server");
			} else if (sendMsgJSONObject.getString("status").equals("fail")) {
				Log.d(API_ERROR_TAG, sendMsgJSONObject.getString("error"));
			}
		} catch (JSONException exception) {
			Log.d(JSON_ERROR_TAG, "Can't send message to server");
		}
	}

	public String getVKUser(String accessToken) {
		JSONArray vkuserObj;
		String vkUserID = "";
		try {
			StringBuilder vkStringBuilder = new StringBuilder();
			vkStringBuilder.append(HMAConsts.VK_PREFIX).append(accessToken);
			vkuserObj = apirequest.doJSONGetRequesOKHTTP(
					vkStringBuilder.toString()).getJSONArray("response");
			JSONObject response = vkuserObj.getJSONObject(0);
			vkUserID = response.getString("id");
		} catch (JSONException e) {
		}
		return vkUserID;
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, Void>() {
			String regid = "";

			@Override
			protected Void doInBackground(Void... params) {
				GoogleCloudMessaging gcm = GoogleCloudMessaging
						.getInstance(context);
				try {
					regid = gcm.register("598373986171");
					Log.d("regid", regid);

				} catch (IOException e) {
				}

				Bundle adBundle = new Bundle();
				adBundle.putString("gcmToken", regid);
				AddDeviceTask addDeviceTask = new AddDeviceTask(context);
				addDeviceTask.execute(adBundle);
				return null;
			}

		}.execute(null, null, null);
	}

	public String getFBToken(String code) {

		return apirequest.doPlainTextGetRequest(HMAConsts.FB_TOKEN_URL + code);
	}

	public String getFBUserID(String aT) {
		String fbUID = "";
		try {
			fbUID = apirequest.doJSONGetRequesOKHTTP(
					"https://graph.facebook.com/me?access_token=" + aT)
					.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fbUID;
	}

}
