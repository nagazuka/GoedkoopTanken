package com.nagazuka.mobile.android.goedkooptanken.util;

import android.content.Intent;
import android.net.Uri;

import com.nagazuka.mobile.android.goedkooptanken.model.Place;

public class PlacesUtil {

	public static Intent getGoogleMapsIntent(Place selectedItem) {
		Uri geoUri = createGeoURI(selectedItem, false);
		Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);
		return mapCall;
	}

	public static Intent getGoogleNavigationIntent(Place selectedItem) {
		Uri geoUri = createGeoURI(selectedItem, true);
		Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);
		return mapCall;
	}

	public static Uri createGeoURI(Place selectedItem, boolean navigation) {
		String geoUriString;
		if (!navigation) {
			geoUriString = "geo:0,0?q=Nederland, ";
		} else {
			geoUriString = "google.navigation:q=Nederland, ";
		}

		geoUriString += selectedItem.getAddress() + ", "
				+ selectedItem.getPostalCode() + "," + selectedItem.getTown();

		Uri geoUri = Uri.parse(geoUriString);
		return geoUri;
	}
}