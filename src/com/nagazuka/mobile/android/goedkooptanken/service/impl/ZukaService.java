package com.nagazuka.mobile.android.goedkooptanken.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.nagazuka.mobile.android.goedkooptanken.exception.GoedkoopTankenException;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.model.PlacesParams;
import com.nagazuka.mobile.android.goedkooptanken.service.DownloadService;

public class ZukaService implements DownloadService {

	private static final String TAG = "PlacesDownloader";

	private static final String URL_ZUKASERVICE = "http://zukaservice.appspot.com/goedkooptanken";

	private static final String JSON_RESULTS = "results";
	private static final String JSON_ADDRESS = "address";
	private static final String JSON_NAME = "name";
	private static final String JSON_PRICE = "price";
	private static final String JSON_CONTEXT = "context";
	private static final String JSON_CONTEXT_RESULT = "result";
	private static final String JSON_POSTAL_CODE = "postalCode";
	private static final String JSON_DISTANCE = "distance";
	private static final String JSON_TOWN = "town";

	public List<Place> fetchPlaces(PlacesParams params)
			throws GoedkoopTankenException {
		List<Place> result = Collections.emptyList();
		try {
			String response = download(params);
			result = convertFromJSON(response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GoedkoopTankenException(
					"Could not download places from ZukaService", e);
		}
		return result;
	}

	public String download(PlacesParams params) throws ClientProtocolException,
			IOException {
		String response = "";

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(constructURL(params));
		Log.i(TAG, "<< HTTP Request: " + request.toString());

		ResponseHandler<String> handler = new BasicResponseHandler();
		response = httpClient.execute(request, handler);
		Log.i(TAG, "<< HTTP Response: " + response);

		httpClient.getConnectionManager().shutdown();
		return response;
	}

	private static String constructURL(PlacesParams params) {
		String URL = URL_ZUKASERVICE;
		String postcode = params.getPostcode();
		if (postcode != null && postcode.length() > 4) {
			postcode = postcode.substring(0, 4);
		}
		
		String combinedParams = "?brandstof="
				+ URLEncoder.encode(params.getBrandstof()) + "&postcode="
				+ postcode;
		return URL + combinedParams;
	}

	public List<Place> convertFromJSON(String response) throws JSONException, GoedkoopTankenException {
		List<Place> result = Collections.emptyList();

		JSONObject jsonResponse = new JSONObject(response);

		if (jsonResponse.has(JSON_CONTEXT)) {
			JSONObject context = jsonResponse.getJSONObject(JSON_CONTEXT);
			if (!getJSONString(context, JSON_CONTEXT_RESULT).equals("Success")) {
				throw new GoedkoopTankenException("ZukaService returned failed response", null);
			}
		}

		if (jsonResponse.has(JSON_RESULTS)) {
			result = new ArrayList<Place>();
			JSONArray jsonPlaces = jsonResponse.getJSONArray(JSON_RESULTS);

			for (int i = 0; i < jsonPlaces.length(); i++) {
				JSONObject place = jsonPlaces.getJSONObject(i);

				String address = getJSONString(place, JSON_ADDRESS);
				String postalCode = getJSONString(place, JSON_POSTAL_CODE);
				String town = getJSONString(place, JSON_TOWN);
				String name = getJSONString(place, JSON_NAME);
				double price = getJSONDouble(place, JSON_PRICE);
				double distance = getJSONDouble(place, JSON_DISTANCE);

				result.add(new Place(name, address, postalCode, town, price,
						distance));
			}
		}
		return result;
	}

	private String getJSONString(JSONObject json, String key)
			throws JSONException {
		String result = "";

		if (json.has(key)) {
			result = json.getString(key);
		}

		return result;
	}

	private double getJSONDouble(JSONObject json, String key)
			throws JSONException {
		double result = 0.0;
		if (json.has(key)) {
			result = json.getDouble(key);
		}
		return result;
	}
}
