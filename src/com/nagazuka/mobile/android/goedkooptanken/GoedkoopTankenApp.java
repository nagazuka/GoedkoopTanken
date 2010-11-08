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

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.nagazuka.mobile.android.goedkooptanken.model.Place;

public class GoedkoopTankenApp extends Application {

	private static GoedkoopTankenApp instance;

	private Location location = null;
	private String postalCode = "";
	private String fuelChoice = "";
	private List<Place> places = null;

	public GoedkoopTankenApp() {
		setInstance(this);
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	public List<Place> getPlaces() {
		return places;
	}
	
	public void setFuelChoice(String fuelChoice) {
		this.fuelChoice = fuelChoice;
	}

	public String getFuelChoice() {
		return fuelChoice;
	}

	public static Context getContext() {
		return instance.getApplicationContext();
	}

	private static void setInstance(GoedkoopTankenApp app) {
		instance = app;
	}
}