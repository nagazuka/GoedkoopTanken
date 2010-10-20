package com.nagazuka.mobile.android.goedkooptanken;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.nagazuka.mobile.android.goedkooptanken.R;

public class GoedkoopTankenActivity extends Activity {

	private GoedkoopTankenApp app;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		app = (GoedkoopTankenApp) getApplication();
		
	    Spinner spinner = (Spinner) findViewById(R.id.spinner);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.gastypes, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);

		Button gpsButton = (Button) findViewById(R.id.gpsButton);
		gpsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent switchIntent = new Intent(v.getContext(),
						PlacesTabActivity.class);
				Spinner spinner = (Spinner) findViewById(R.id.spinner);
				String fuelChoice = (String) spinner.getSelectedItem();
				app.setFuelChoice(fuelChoice);
				app.setPlaces(null);
				startActivityForResult(switchIntent, 0);
			}
		});
	}
}