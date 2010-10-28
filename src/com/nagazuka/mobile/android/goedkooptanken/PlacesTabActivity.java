package com.nagazuka.mobile.android.goedkooptanken;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TabHost;

public class PlacesTabActivity extends TabActivity {

	private static final String TAG = "PlacesTabActivity";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TabHost tabHost = getTabHost();
        
        LayoutInflater.from(this).inflate(R.layout.places, tabHost.getTabContentView(), true);

        tabHost.addTab(tabHost.newTabSpec("list")
                .setIndicator("Lijst")
                .setContent(R.id.listview));
        tabHost.addTab(tabHost.newTabSpec("map")
                .setIndicator("Kaart")
                .setContent(R.id.mapview));

		tabHost.setCurrentTab(0);
	}
}