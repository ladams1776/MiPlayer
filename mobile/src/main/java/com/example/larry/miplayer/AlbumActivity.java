package com.example.larry.miplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * WE GOT IT WORKING, EACH MainActivity...ListFragment now jump to this Activity
 * 
 * This Activity is used when we make a choice in the onItemClickListener of the
 * MainActivity...ListFragment Method's
 * 
 * @author larry
 * 
 * 
 * NEED TO FIX
 * 1. In AlbumActivityTitleFragment, we got to adjust the title, if we came from the ArtistListFragment
 */
public class AlbumActivity extends FragmentActivity {

	public static final String KEY_PRIOR_ACTIVITY = "KEY_PRIOR_ACTIVITY";
	// it's either ALBUM or FOLDER

	private FragmentManager fmg;
	boolean isServiceOn = false;
	private AlbumActivityTitleFragment tf;
	private AlbumActivityListFragment lf;
	private ControlPanelMiniFragment cpf;

	// we set this in our onCreate from the Intent that created this Activity,
	// it is our fraqments that grab this constant, and uses different tasks
	// in order to setup the UI.
	public String whatActivityDidWeComeFrom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.fragment1_album_title);
		initBroadcasts();

		fmg = getSupportFragmentManager();
		tf = new AlbumActivityTitleFragment();
		lf = new AlbumActivityListFragment();
		cpf = new ControlPanelMiniFragment();
		fmg.beginTransaction().add(R.id.firstFragment, tf).commit();
		fmg.beginTransaction().add(R.id.secondFragment, lf).commit();
		fmg.beginTransaction().add(R.id.thirdFragment, cpf).commit();

		// Set which activity this came from.
		Intent findPriorActivity = getIntent();
		whatActivityDidWeComeFrom = findPriorActivity.getExtras().getString(
				KEY_PRIOR_ACTIVITY);
		Log.d("Troubleshooting1", whatActivityDidWeComeFrom);

	}

	private void initBroadcasts() {
		registerReceiver(receiveIsServiceIsOn, new IntentFilter(
				NowPlaying.ACTION_IS_SERVICE_ON));
	}

	@Override
	protected void onStart() {
		super.onStart();
		isServiceOn = false;
		if (!cpf.isAdded()) {
			fmg.beginTransaction().add(R.id.thirdFragment, cpf).commit();
		}
		sendBroadcast(new Intent(NowPlaying.ACTION_ASK_SERVICE_ON));
		cpf.notifyFragmentIfServiceIsOnOrOff(isServiceOn);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiveIsServiceIsOn);

	}

	BroadcastReceiver receiveIsServiceIsOn = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			isServiceOn = true;
			cpf.notifyFragmentIfServiceIsOnOrOff(isServiceOn);

		}
	};

}
