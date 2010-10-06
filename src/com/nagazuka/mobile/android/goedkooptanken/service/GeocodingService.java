package com.nagazuka.mobile.android.goedkooptanken.service;

import com.nagazuka.mobile.android.goedkooptanken.exception.GoedkoopTankenException;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;

public interface GeocodingService {
	
	/* Returns postal code for given latitude and longitude */
	public String getPostalCode(double latitude, double longitude) throws GoedkoopTankenException;

	/* Returns latitude and longitude for given location */
	public double[] getLocation(Place place) throws GoedkoopTankenException;

}