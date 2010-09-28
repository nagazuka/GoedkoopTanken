package com.nagazuka.mobile.android.goedkooptanken.model;

public class Place implements Comparable<Place> {
	private double price = 0.0;
	private double distance = 0.0;
	private String name = "";
	private String address = "";

	public Place() {
	}

	public Place(String name, String address, double price, double distance) {
		super();
		this.price = price;
		this.distance = distance;
		this.name = name;
		this.address = address;
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

	@Override
	public int compareTo(Place another) {		
		int diff = (int) (this.getPrice() * 100 - another.getPrice() * 100);
		return diff;
	}

}
