package com.nagazuka.mobile.android.goedkooptanken.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class Place {
	private double price = 0.0;
	private double distance = 0.0;
	private String name = "";
	private String address = "";
	private String town = "";
	private String postalCode = "";
	private String observationDate = "";
	private GeoPoint point = null;

	private int priceIndicator = Place.NORMAL;

	private static final String TAG = Place.class.getName();
	public static final int CHEAP = 1;
	public static final int NORMAL = 2;
	public static final int EXPENSIVE = 3;

	public Place() {
	}

	public Place(String name, String address, String postalCode, String town,
			double price, double distance, String observationDate) {
		super();
		this.price = price;
		this.distance = distance;
		this.postalCode = postalCode;
		this.town = town;
		this.name = name;
		this.address = address;
		this.observationDate = observationDate;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getTown() {
		return town;
	}

	public void setPriceIndicator(int priceIndicator) {
		this.priceIndicator = priceIndicator;
	}

	public int getPriceIndicator() {
		return priceIndicator;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPoint(GeoPoint point) {
		this.point = point;
	}

	public GeoPoint getPoint() {
		return point;
	}

	public String getSummary() {
		String result = getAddress() + "\n";
		result += getPostalCode() + "\n";
		result += getTown() + "\n";
		result += String.format("Literprijs: \u20AC %.2f\n", getPrice());
		result += String.format("Geschatte afstand: %.2f km\n", getDistance());
		result += "Peildatum: " + getObservationDate() + "\n";
		return result;
	}

	public void setObservationDate(String observationDate) {
		this.observationDate = observationDate;
	}

	public String getObservationDate() {
		return observationDate;
	}

	public static List<Place> calculatePriceIndicators(List<Place> places) {
		// select all distinct prices
		// first is cheapest
		// second two are normal
		// the rest is expensive

		List<Double> priceList = new ArrayList<Double>();
		for (Place p : places) {
			priceList.add(p.getPrice());
		}

		// remove duplicate prices
		HashSet<Double> noDuplicates = new HashSet<Double>(priceList);
		priceList.clear();
		priceList.addAll(noDuplicates);

		Collections.sort(priceList);
		// set price indicators based on index in priceList
		for (Place p : places) {
			int index = priceList.indexOf(new Double(p.getPrice()));
			if (index == 0) {
				p.setPriceIndicator(Place.CHEAP);
			} else if (index > 0 && index <= 2) {
				p.setPriceIndicator(Place.NORMAL);
			} else if (index > 2) {
				p.setPriceIndicator(Place.EXPENSIVE);
			}
			Log.d(TAG, "Set price indicator for price [" + p.getPrice()
					+ "] with price list index [" + index + "] to ["
					+ p.getPriceIndicator() + "]");
		}

		return places;
	}
}
