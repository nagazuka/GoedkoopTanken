package com.nagazuka.mobile.android.goedkooptanken;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TabHost;

public class PlacesActivity extends TabActivity {

	private static final int DIALOG_PROGRESS = 1;
	private static int MAX_PROGRESS = 100;

	private ProgressDialog mProgressDialog;

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

		new LocationTask().execute("Euro 95");

		// Tab initialization
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, PlacesListActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("list").setIndicator("Lijst",
				res.getDrawable(R.drawable.ic_tab_list_icons)).setContent(
				intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, PlacesMapActivity.class);
		spec = tabHost.newTabSpec("map").setIndicator("Kaart",
				res.getDrawable(R.drawable.ic_tab_map_icons))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

	private class LocationTask extends AsyncTask<String, Integer, String> {
		private int mProgress;

		@Override
		public void onPreExecute() {
			mProgress = 0;
			showDialog(DIALOG_PROGRESS);
			mProgressDialog.setProgress(mProgress);

		}

		@Override
		protected String doInBackground(String... arg0) {
			for (int i = 1; i <= MAX_PROGRESS; i++) {
				mProgress++;
				publishProgress(mProgress);
			}
			return "23L440N";
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			mProgressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			mProgress = MAX_PROGRESS;
			mProgressDialog.setProgress(mProgress);
			mProgressDialog.dismiss();
		}

	}

}
