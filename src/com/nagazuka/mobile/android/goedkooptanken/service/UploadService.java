package com.nagazuka.mobile.android.goedkooptanken.service;

import java.util.List;

import com.nagazuka.mobile.android.goedkooptanken.exception.GoedkoopTankenException;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;

public interface UploadService {
	public void uploadPlaces(List<Place> places) throws GoedkoopTankenException;
}
