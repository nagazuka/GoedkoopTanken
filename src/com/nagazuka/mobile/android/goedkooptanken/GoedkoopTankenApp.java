package com.nagazuka.mobile.android.goedkooptanken;

import java.util.Collections;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.nagazuka.mobile.android.goedkooptanken.model.Place;

public class GoedkoopTankenApp extends Application {

	private static GoedkoopTankenApp instance;

	private Location location = null;
	private String postalCode = "";
	private List<Place> places = Collections.emptyList();

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

	public static Context getContext() {
		return instance.getApplicationContext();
	}

	private static void setInstance(GoedkoopTankenApp app) {
		instance = app;
	}
}