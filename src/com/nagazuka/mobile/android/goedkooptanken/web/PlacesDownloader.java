package com.nagazuka.mobile.android.goedkooptanken.web;

import java.util.List;

import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.model.PlacesParams;

public interface PlacesDownloader {
	public List<Place> fetchPlaces(PlacesParams params);
}
