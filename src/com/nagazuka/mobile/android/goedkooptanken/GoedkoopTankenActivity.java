package com.nagazuka.mobile.android.goedkooptanken;

import com.nagazuka.mobile.android.goedkooptanken.model.PlacesConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class GoedkoopTankenActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
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
						PlacesActivity.class);
				Spinner spinner = (Spinner) findViewById(R.id.spinner);
				String gasType = (String) spinner.getSelectedItem();
				switchIntent.putExtra(PlacesConstants.INTENT_EXTRA_FUEL_CHOICE, gasType);
				startActivityForResult(switchIntent, 0);
			}
		});
	}
}