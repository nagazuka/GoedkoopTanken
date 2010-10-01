package com.nagazuka.mobile.android.goedkooptanken.service;

import java.util.List;

import com.nagazuka.mobile.android.goedkooptanken.exception.GoedkoopTankenException;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.model.PlacesParams;

public interface DownloadService {
	public List<Place> fetchPlaces(PlacesParams params) throws GoedkoopTankenException;
}
