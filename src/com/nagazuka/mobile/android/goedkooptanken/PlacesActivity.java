package com.nagazuka.mobile.android.goedkooptanken;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TabHost;

public class PlacesActivity extends TabActivity {

	private static final int DIALOG_PROGRESS = 1;
	private static int MAX_PROGRESS = 100;

	private int mProgress;
	private ProgressDialog mProgressDialog;
	private Handler mProgressHandler;

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_PROGRESS:
			mProgressDialog = new ProgressDialog(PlacesActivity.this);
			mProgressDialog.setIcon(R.drawable.ic_gps_satellite);
			mProgressDialog.setTitle(R.string.progressdialog_title);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(MAX_PROGRESS);
			mProgressDialog.setButton2(getText(R.string.progressdialog_cancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							/* User clicked No so do some stuff */
						}
					});
			dialog = mProgressDialog;
		}
		return dialog;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.places);

		// Progress bar initialization

		showDialog(DIALOG_PROGRESS);
		mProgress = 0;
		mProgressDialog.setProgress(mProgress);

		mProgressHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (mProgress >= MAX_PROGRESS) {
					mProgressDialog.dismiss();
				} else {
					mProgress++;
					mProgressDialog.incrementProgressBy(1);
					mProgressHandler.sendEmptyMessageDelayed(0, 100);
				}
			}
		};

		mProgressHandler.sendEmptyMessage(0);

		// Tab initialization

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, PlacesListActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("list").setIndicator("Lijst",
				res.getDrawable(R.drawable.ic_tab_list_icons)).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, PlacesMapActivity.class);
		spec = tabHost.newTabSpec("map").setIndicator("Kaart",
				res.getDrawable(R.drawable.ic_tab_map_icons)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

}
