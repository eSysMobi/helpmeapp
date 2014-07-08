package mobi.esys.fragments;

import mobi.esys.constants.HMAConsts;
import mobi.esys.helpmeapp.LoginActivity;
import mobi.esys.helpmeapp.MainActivity;
import mobi.esys.helpmeapp.NOActivity;
import mobi.esys.helpmeapp.R;
import android.app.ListFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuFragment extends ListFragment {
	private transient SharedPreferences prefs;
	private static final int NOTIFY_ID = HMAConsts.WORKING_NOTIFICATION_ID;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		prefs = getActivity().getSharedPreferences(HMAConsts.HMA_PREF,
				Context.MODE_PRIVATE);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, getActivity()
						.getResources().getStringArray(R.array.menuItems));
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (position == 0) {
			getActivity().finish();
			getActivity().startActivity(
					new Intent(getActivity(), MainActivity.class).putExtra(
							"isFromNOActivity", true));
		} else if (position == 1) {
			((NOActivity) getActivity()).stopTracking();
		} else if (position == 2) {
			((NOActivity) getActivity()).stopTracking();
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(HMAConsts.HMA_PREF_API_KEY, "");
			editor.putString(HMAConsts.HMA_PREF_USER_ID, "");
			editor.commit();
			getActivity().startActivity(
					new Intent(getActivity(), LoginActivity.class));
			getActivity().finish();
		} else {
			((NOActivity) getActivity()).stopTracking();
			NotificationManager nm = (NotificationManager) getActivity()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(NOTIFY_ID);
			getActivity().finish();
			((NOActivity) getActivity()).finish();
			closeMain(getActivity());
		}
		((NOActivity) getActivity()).getSlidingMenu().toggle();
	}

	public static void closeMain(Context context) {
		Intent intent = new Intent("MainActivity");
		intent.putExtra("action", "close");
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

}
