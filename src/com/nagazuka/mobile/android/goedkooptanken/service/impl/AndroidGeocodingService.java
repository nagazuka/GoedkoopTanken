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
import com.nagazuka.mobile.android.goedkooptanken.exception.NetworkException;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.service.GeocodingService;

public class AndroidGeocodingService implements GeocodingService {
	private static final String TAG = AndroidGeocodingService.class.getName();

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
			Log.e(TAG, "<< Error looking up address with Geocoder for lat ["
					+ latitude + "] lng [" + longitude + "] >>");
			e.printStackTrace();
			throw new NetworkException(
					"Uw postcode kan niet bepaald worden. Toegang tot het internet is vereist.",
					e);
		}

		if (adresses != null && !adresses.isEmpty()) {
			Address address = adresses.get(0);
			postalCode = address.getPostalCode();
			Log.d(TAG, "<< Geocoder found postalCode: " + postalCode + ">>");
		} else {
			throw new NetworkException(
					"Uw postcode kan niet bepaald worden. Onbekende fout opgetreden.",
					null);
		}

		return postalCode;
	}

	@Override
	public double[] getLocation(Place place) throws GoedkoopTankenException {
		double[] result = { 0.0, 0.0 };

		int maxResults = 1;
		String address = place.getAddress();
		String postalCode = place.getPostalCode();
		String town = place.getTown();
		String locationName = address + ", " + postalCode + ", " + town;

		try {
			Context context = GoedkoopTankenApp.getContext();
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocationName(
					locationName, maxResults);
			if (addresses != null && !addresses.isEmpty()) {
				Address location = addresses.get(0);
				result[0] = location.getLatitude();
				result[1] = location.getLongitude();
			}
		} catch (Exception e) {
			Log.e(TAG, "<< Error looking up location name [" + locationName
					+ "] with Geocoder >>");
			e.printStackTrace();
			throw new GoedkoopTankenException("Could not lookup location ["
					+ locationName + "]with Google Geocoder", e);
		}
		return result;
	}
}