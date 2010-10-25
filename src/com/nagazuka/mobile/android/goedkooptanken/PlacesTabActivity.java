package com.nagazuka.mobile.android.goedkooptanken;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class PlacesTabActivity extends TabActivity {

	private static final String TAG = "PlacesTabActivity";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places);

		// Tab initialization
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, PlacesListActivity.class);

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
}