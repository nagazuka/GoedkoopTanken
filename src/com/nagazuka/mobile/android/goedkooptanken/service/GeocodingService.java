package com.nagazuka.mobile.android.goedkooptanken.service;

import com.nagazuka.mobile.android.goedkooptanken.exception.GoedkoopTankenException;

public interface GeocodingService {

	public String getPostalCode(double latitude, double longitude) throws GoedkoopTankenException;

}
