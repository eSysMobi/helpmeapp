package mobi.esys.utils;

import mobi.esys.constants.HMAConsts;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TwitterUtils {
	public static Twitter isAuth(SharedPreferences prefs) {
		String token = prefs.getString("TW_TOKEN", "");
		String secret = prefs.getString("TW_SECRET", "");

		if (token == null || token.length() == 0 || secret == null
				|| secret.length() == 0) {
			return null;
		}

		try {
			AccessToken accessToken = new AccessToken(token, secret);
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(HMAConsts.CONSUMER_KEY,
					HMAConsts.CONSUMER_SECRET);
			twitter.setOAuthAccessToken(accessToken);
			return twitter;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static void saveAccessToken(SharedPreferences prefs,
			AccessToken accessToken) {
		final Editor editor = prefs.edit();
		editor.putString("TW_TOKEN", accessToken.getToken());
		editor.putString("TW_SECRET", accessToken.getTokenSecret());
		editor.commit();
	}

}
