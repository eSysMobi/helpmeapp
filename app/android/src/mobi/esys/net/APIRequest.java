package mobi.esys.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class APIRequest {
	private transient InputStream inputStream;

	public JSONObject doJSONGetRequesOKHTTP(String requestURL) {
		Log.d("url", requestURL);
		JSONObject jsonObject = new JSONObject();
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(requestURL).build();
			Response response = client.newCall(request).execute();
			jsonObject = new JSONObject(response.body().string());
		} catch (IOException e) {
		} catch (JSONException e) {
		}
		Log.d("jsonObject", jsonObject.toString());
		return jsonObject;
	}

	public JSONObject doJSONGetRequest(String requestURL) {
		Log.d("url", requestURL);

		InputStream inputStream = new InputStream() { // NOPMD by Àðò¸ì on
														// 03.06.13 13:02

			@Override
			public int read() throws IOException {
				return 0;
			}
		};

		String result = "";
		JSONObject jsonObject = new JSONObject();

		try {
			final HttpClient httpClient = new DefaultHttpClient();
			final HttpGet httpGet = new HttpGet(requestURL);
			Log.d("req url", requestURL);

			final HttpResponse httpResponse = httpClient.execute(httpGet);
			final HttpEntity httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();

			final BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			final StringBuilder stringBuilder = new StringBuilder();
			String line = "";

			while (line != null) {
				line = bufferedReader.readLine();
				stringBuilder.append(line + "\n");
			}

			inputStream.close();

			result = stringBuilder.toString();

			Log.d("req res", result);

			jsonObject = new JSONObject(result);
			Log.d("req res", jsonObject.toString());
		} catch (Exception Exception) {
		}
		return jsonObject;

	}

	public JSONArray doJSONArrayGetRequest(String requestURL) {

		inputStream = new InputStream() { // NOPMD by Àðò¸ì on
											// 03.06.13 13:02

			@Override
			public int read() throws IOException {
				return 0;
			}
		};

		String result = "";
		JSONArray jsonObject = new JSONArray();

		try {
			final HttpClient httpClient = new DefaultHttpClient();
			final HttpGet httpGet = new HttpGet(requestURL);
			Log.d("req url", requestURL);

			final HttpResponse httpResponse = httpClient.execute(httpGet);
			final HttpEntity httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();

			final BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			final StringBuilder stringBuilder = new StringBuilder();
			String line = "";

			while (line != null) {
				line = bufferedReader.readLine();
				stringBuilder.append(line + "\n");
			}

			inputStream.close();

			result = stringBuilder.toString();

			jsonObject = new JSONArray(result);
		} catch (Exception Exception) {
		}
		return jsonObject;
	}

	public String doPlainTextGetRequest(String requestURL) {

		inputStream = new InputStream() { // NOPMD by Àðò¸ì on
											// 03.06.13 13:02

			@Override
			public int read() throws IOException {
				return 0;
			}
		};

		String result = "";

		try {
			final HttpClient httpClient = new DefaultHttpClient();
			final HttpGet httpGet = new HttpGet(requestURL);
			Log.d("req url", requestURL);

			final HttpResponse httpResponse = httpClient.execute(httpGet);
			final HttpEntity httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();

			final BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			final StringBuilder stringBuilder = new StringBuilder();
			String line = "";

			while (line != null) {
				line = bufferedReader.readLine();
				stringBuilder.append(line + "\n");
			}

			inputStream.close();

			result = stringBuilder.toString();

		} catch (Exception Exception) {
		}
		return result;
	}

}
