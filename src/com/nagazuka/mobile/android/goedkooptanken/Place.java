package com.nagazuka.mobile.android.goedkooptanken;

public class Place {
	private double price = 0.0;
	private String name = "";
	private String address = "";
	
	public Place() {
	}
	
	public Place(String name, String address, double price) {
		super();
		this.price = price;
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
}
