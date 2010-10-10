package com.nagazuka.mobile.android.goedkooptanken.model;

import java.util.Comparator;

public class PlacePriceDistanceComparator implements Comparator<Place> {

	@Override
	public int compare(Place p0, Place p1) {
		int priceDiff = (int) (1000 * p0.getPrice())
				- (int) (1000 * p1.getPrice());
		if (priceDiff == 0) {
			return (int) (10 * p0.getDistance())
					- (int) (10 * p1.getDistance());
		} else {
			return priceDiff;
		}
	}
}
