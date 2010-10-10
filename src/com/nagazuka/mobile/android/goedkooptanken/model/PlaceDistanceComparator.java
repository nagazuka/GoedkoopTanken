package com.nagazuka.mobile.android.goedkooptanken.model;

import java.util.Comparator;

public class PlaceDistanceComparator implements Comparator<Place> {

	@Override
	public int compare(Place p0, Place p1) {
		return (int) (100*p0.getDistance()) - (int) (100*p1.getDistance());
	}
}
