package mobi.esys.adapters;

import mobi.esys.helpmeapp.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

public class NumSpinWheelArrayAdapter extends ArrayWheelAdapter<Integer> {
	private transient Integer[] items;

	public NumSpinWheelArrayAdapter(Context context, Integer[] items) {
		super(context, items);
		this.items = items;
	}

	@Override
	public int getItemsCount() {
		return items.length;
	}

	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {

		View view = super.getItem(index, cachedView, parent);

		TextView pickerText = (TextView) view.findViewById(R.id.pickerText);
		pickerText.setText(String.valueOf(items[index]));
		return view;
	}

	@Override
	public CharSequence getItemText(int index) {
		return String.valueOf(items[index]);
	}

}
