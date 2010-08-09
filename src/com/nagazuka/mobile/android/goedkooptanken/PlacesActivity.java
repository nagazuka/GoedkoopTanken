package com.nagazuka.mobile.android.goedkooptanken;

import java.util.Collections;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class PlacesActivity extends TabActivity {

	private static final String TAG = "PlacesActivity";
	private static final int DIALOG_PROGRESS = 1;
	private static final int MAX_PROGRESS = 100;
	private String mPostalCode = "";
	private String mFuelChoice = "";
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
	
	private void startTabActivities() {
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.places);

		mFuelChoice = getIntent().getStringExtra(
				PlacesConstants.INTENT_EXTRA_FUEL_CHOICE);

		new LocationTask().execute();
		new DownloadTask().execute(mFuelChoice, mPostalCode);
		
		// Tab initialization
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, PlacesListActivity.class);
		
		System.out.println("<< LOG mFuelChoice " + mFuelChoice +" mPostalCode " + mPostalCode +">>");
		intent.putExtra(PlacesConstants.INTENT_EXTRA_FUEL_CHOICE, mFuelChoice);
		intent.putExtra(PlacesConstants.INTENT_EXTRA_POSTAL_CODE, mPostalCode);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("list").setIndicator(
				res.getString(R.string.results_label),
				res.getDrawable(R.drawable.ic_tab_list_icons)).setContent(
				intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, PlacesMapActivity.class);
		spec = tabHost.newTabSpec("map").setIndicator(
				res.getString(R.string.map_label),
				res.getDrawable(R.drawable.ic_tab_map_icons))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

	private class LocationTask extends AsyncTask<Void, Integer, String> {
		private int mProgress;

		@Override
		public void onPreExecute() {
			mProgress = 0;
			showDialog(DIALOG_PROGRESS);
			mProgressDialog.setProgress(mProgress);

		}

		@Override
		protected String doInBackground(Void... params) {
			for (int i = 1; i <= MAX_PROGRESS; i++) {
				mProgress++;
				publishProgress(mProgress);
			}
			return "2281 BN";
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
			
			mPostalCode = result;
			
			Log.d(TAG,"<< mFuelChoice " + mFuelChoice +" mPostalCode " + mPostalCode +">>");
		}

	}

	private class DownloadTask extends AsyncTask<String, Integer, List<String>> {

		@Override
		protected List<String> doInBackground(String... params) {
			List<String> results = Collections.emptyList();
			return results;
		}
		
		@Override
		protected void onPostExecute(List<String> result) {
			startTabActivities();
		}
	}

}