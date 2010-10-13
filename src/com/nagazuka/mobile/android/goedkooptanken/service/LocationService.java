package com.nagazuka.mobile.android.goedkooptanken.service;

import android.location.Location;
import android.location.LocationManager;

import com.nagazuka.mobile.android.goedkooptanken.exception.GoedkoopTankenException;

public interface LocationService {

	/* Returns latitude and longitude for current location */
	Location getCurrentLocation(LocationManager locationManager)
			throws GoedkoopTankenException;

}
