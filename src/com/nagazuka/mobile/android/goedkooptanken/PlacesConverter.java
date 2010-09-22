package com.nagazuka.mobile.android.goedkooptanken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlacesConverter {

	private static final String JSON_RESULTS = "results";
	private static final String JSON_ADDRESS = "address";
	private static final String JSON_NAME = "name";
	private static final String JSON_PRICE = "price";

	public static List<Place> convertFromJSON(JSONObject jsonResponse) {
		List<Place> result = Collections.emptyList();

		try {
			if (jsonResponse.has(JSON_RESULTS)) {
				result = new ArrayList<Place>();
				JSONArray jsonPlaces = jsonResponse.getJSONArray(JSON_RESULTS);

				for (int i = 0; i < jsonPlaces.length(); i++) {
					JSONObject place = jsonPlaces.getJSONObject(i);

					String address = place.getString(JSON_ADDRESS);
					String name = place.getString(JSON_NAME);
					double price = parsePrice(place.getString(JSON_PRICE));

					result.add(new Place(address, name, price));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static double parsePrice(String string) {
		// TODO Auto-generated method stub
		return 1.5;
	}
}
