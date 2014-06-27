package mobi.esys.helpmeapp;

import mobi.esys.constants.HMAConsts;
import mobi.esys.fragments.MenuFragment;
import mobi.esys.tasks.DisableTrackingTask;
import mobi.esys.tasks.EnableTrackingTask;
import mobi.esys.tasks.SendOKTask;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

public class NOActivity extends SlidingActivity implements OnClickListener {
	private transient ImageView menuBtn;
	private transient ImageView cancelBtn;
	private transient SharedPreferences preferences;
	private transient ListFragment menuFragment;
	private transient boolean isStoped;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBehindContentView(R.layout.menu_layout);
		setContentView(R.layout.activity_cancel_warning);

		preferences = getSharedPreferences(HMAConsts.HMA_PREF, MODE_PRIVATE);
		isStoped = preferences.getBoolean("isStoped", true);
		menuFragment = new MenuFragment();

		final SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setBehindOffset(300);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getFragmentManager().beginTransaction()
				.replace(R.id.menuCnt, menuFragment, "menuFrag").commit();

		menuBtn = (ImageView) findViewById(R.id.menuBtn);
		menuBtn.setOnClickListener(this);

		cancelBtn = (ImageView) findViewById(R.id.stopMsgBtn);
		cancelBtn.setOnClickListener(this);
	}

	public void stopTracking() {
		if (!isStoped) {
			DisableTrackingTask disableTracking = new DisableTrackingTask(
					NOActivity.this);
			disableTracking.execute();

			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("isStoped", true);
			editor.commit();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.menuBtn) {
			getSlidingMenu().toggle();
		} else {
			SendOKTask okTask = new SendOKTask(NOActivity.this);
			okTask.execute();

			EnableTrackingTask enableTracking = new EnableTrackingTask(
					NOActivity.this);
			enableTracking.execute();
		}
	}
}
