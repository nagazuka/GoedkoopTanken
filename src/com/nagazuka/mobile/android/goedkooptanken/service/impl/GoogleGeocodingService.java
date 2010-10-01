package com.nagazuka.mobile.android.goedkooptanken.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.nagazuka.mobile.android.goedkooptanken.GoedkoopTankenApp;
import com.nagazuka.mobile.android.goedkooptanken.exception.GoedkoopTankenException;
import com.nagazuka.mobile.android.goedkooptanken.service.GeocodingService;

public class GoogleGeocodingService implements GeocodingService {
	private static final String TAG = GoogleGeocodingService.class.getName();

	@Override
	public String getPostalCode(double latitude, double longitude)
			throws GoedkoopTankenException {
		String postalCode = "";
		Context context = GoedkoopTankenApp.getContext();

		int maxResults = 1;
		// Transform location to address using reverse geocoding
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		List<Address> adresses = Collections.emptyList();
		try {
			adresses = geocoder
					.getFromLocation(latitude, longitude, maxResults);
		} catch (IOException e) {
			Log.e(TAG, "<< Error looking up address with Geocoder >>");
			e.printStackTrace();
			throw new GoedkoopTankenException(
					"Could not lookup address with Google Geocoder", e);
		}

		if (!adresses.isEmpty()) {
			Address address = adresses.get(0);
			postalCode = address.getPostalCode();
			Log.d(TAG, "<< Geocoder found postalCode: " + postalCode + ">>");
		}

		return postalCode;
	}
}
