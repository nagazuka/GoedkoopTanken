package com.nagazuka.mobile.android.goedkooptanken;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GoedkoopTankenActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		Button gpsButton = (Button) findViewById(R.id.gpsButton);
		gpsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent switchIntent = new Intent(v.getContext(),
						PlacesActivity.class);
				startActivityForResult(switchIntent, 0);
			}
		});
	}
}