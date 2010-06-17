package com.nagazuka.mobile.android.goedkooptanken;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PlacesMapActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView textview = new TextView(this);
		textview.setText("This is the Maps tab");
		setContentView(textview);
	}
}
