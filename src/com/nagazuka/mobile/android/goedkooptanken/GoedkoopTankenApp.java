package com.nagazuka.mobile.android.goedkooptanken;

import android.app.Application;
import android.content.Context;
import android.location.Location;

public class GoedkoopTankenApp extends Application {

	private static GoedkoopTankenApp instance;
	
	private Location location = null;
	private String postalCode = "";
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public GoedkoopTankenApp() {
		instance = this;
	}
	
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPostalCode() {
		return postalCode;
	}
	
	public static Context getContext() {
		return instance.getApplicationContext();		
	}
}
