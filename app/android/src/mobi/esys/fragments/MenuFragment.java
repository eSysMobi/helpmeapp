package mobi.esys.fragments;

import mobi.esys.helpmeapp.MainActivity;
import mobi.esys.helpmeapp.NOActivity;
import mobi.esys.helpmeapp.R;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
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
		} else {
			((NOActivity) getActivity()).stopTracking();
		}
	}

}
