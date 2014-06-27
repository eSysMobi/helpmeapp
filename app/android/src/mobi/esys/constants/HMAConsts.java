package mobi.esys.constants;

public class HMAConsts {
	public static final String HMA_PREF = "HMAPref";
	public static final String HMA_PREF_USER_ID = "userID";
	public static final String HMA_PREF_API_KEY = "apiKey";
	public static final String API_USER_PREFIX = "http://helpmeapp.mobi/api/user/";
	public static final String API_USER_LOGIN_REG_UPDATE = "login?";
	public static final String API_USER_SET_LIMITS = "setlimits?";
	public static final String API_USER_ADD_RECORD = "addrecord?";
	public static final String API_USER_SENDMESSAGE = "sendmessage/ok?";
	public static final String API_DEVICE_PREFIX = "http://helpmeapp.mobi/api/device/";
	public static final String API_DEVICE_DISABLE_TRACKING = "tracking/disable?";
	public static final String API_DEVICE_ENABLE_TRACKING = "tracking/enable?";
	public static final String API_DEVICE_ADD_DEVICE = "add?";
	public static final String PARAM_TOKEN = "&token=";
	public static final String VK_PREFIX = "https://api.vk.com/method/users.get?v=5.21&access_token=";
	public static final String VK_LOGIN_URL = "https://oauth.vk.com/authorize?client_id=4396881&redirect_uri=https://oauth.vk.com/blank.html&display=mobile&v=5.21&response_type=token&revoke=1";
	public static final String FB_LOGIN_URL = "https://www.facebook.com/dialog/oauth?client_id=579394165512081&redirect_uri=https://www.facebook.com/connect/login_success.html&scope=publish_actions";
	public static final String FB_TOKEN_URL = "https://graph.facebook.com/oauth/access_token?client_id=579394165512081&redirect_uri=https://www.facebook.com/connect/login_success.html&client_secret=dbafbd72db6923f5a75f3507a0deb06e&code=";
	public static final String TW_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String TW_ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	public static final String TW_AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
	public static final String CONSUMER_KEY = "JT8xafimiH941LM3xC4umLPdu";
	public static final String CONSUMER_SECRET = "0TKoZNTaV0wnMSoEFGTWf16RpDuwJCOJnWHMlHwDT6MLfTpCmX";

	public static final int SEND_DELAY = 60000;
	public static final int REQUEST_LOCATION_DIST = 10;
	public static final int WORKING_NOTIFICATION_ID = 101;
	public static final int GCM_NOTIFICATION_ID = 102;

}
