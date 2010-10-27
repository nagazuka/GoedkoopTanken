package com.nagazuka.mobile.android.goedkooptanken;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;

public class GoedkoopTankenApp extends Application {

	private static GoedkoopTankenApp instance;
	private static GoogleAnalyticsTracker tracker = null;

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
	
	public static GoogleAnalyticsTracker getTracker() {
		if (tracker == null) {
		    tracker = GoogleAnalyticsTracker.getInstance();
		}
		return tracker;
	}

	private static void setInstance(GoedkoopTankenApp app) {
		instance = app;
	}
}