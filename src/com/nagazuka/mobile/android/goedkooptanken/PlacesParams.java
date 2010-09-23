package com.nagazuka.mobile.android.goedkooptanken;

public class PlacesParams {
	private String brandstof = "";
	private String postcode = "";

	public PlacesParams(String brandstof, String postcode) {
		super();
		this.brandstof = brandstof;
		this.postcode = postcode;
	}

	public String getBrandstof() {
		return brandstof;
	}

	public void setBrandstof(String brandstof) {
		this.brandstof = brandstof;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

}
