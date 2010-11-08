/*   
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.nagazuka.mobile.android.goedkooptanken;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class GoedkoopTankenActivity extends Activity {
	public static final String PREFERENCE_FILENAME = "GoedkoopTankenPreferences";

	private GoedkoopTankenApp app;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		app = (GoedkoopTankenApp) getApplication();

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFERENCE_FILENAME,
				0);
		String prefFuelChoice = settings.getString("fuelChoice", null);

		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.gastypes, android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		if (prefFuelChoice != null) {
			int position = adapter.getPosition(prefFuelChoice);
			spinner.setSelection(position);
		}

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

				// We need an Editor object to make preference changes.
				// All objects are from android.context.Context
				SharedPreferences settings = getSharedPreferences(PREFERENCE_FILENAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("fuelChoice", fuelChoice);

				// Commit the edits!
				editor.commit();

				startActivityForResult(switchIntent, 0);
			}
		});
	}
}