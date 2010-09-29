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

	public List<Place> fetchPlaces(PlacesParams params) {
		String response = download(params);

		List<Place> result = convertFromJSON(response);
		return result;
	}

	public String download(PlacesParams params) {
		HttpClient httpClient = new DefaultHttpClient();

		HttpGet request = new HttpGet(constructURL(params));

		Log.i(TAG, "<< HTTP Request: " + request.toString());

		String response = "";
		ResponseHandler<String> handler = new BasicResponseHandler();

		try {
			response = httpClient.execute(request, handler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		httpClient.getConnectionManager().shutdown();

		Log.i(TAG, "<< HTTP Response: " + response);

		return response;
	}

	private static String constructURL(PlacesParams params) {
		String URL = URL_ZUKASERVICE;
		String combinedParams = "?brandstof="
				+ URLEncoder.encode(params.getBrandstof()) + "&postcode="
				+ params.getPostcode();
		return URL + combinedParams;
	}

	public List<Place> convertFromJSON(String response) {
		List<Place> result = Collections.emptyList();

		try {
			JSONObject jsonResponse = new JSONObject(response);

			if (jsonResponse.has(JSON_CONTEXT)) {
				JSONObject context = jsonResponse.getJSONObject(JSON_CONTEXT);
				if (!context.has(JSON_CONTEXT_RESULT)
						|| !context.getString(JSON_CONTEXT_RESULT).equals(
								"Success")) {
					// THROW EXCEPTION
				}
			}

			if (jsonResponse.has(JSON_RESULTS)) {
				result = new ArrayList<Place>();
				JSONArray jsonPlaces = jsonResponse.getJSONArray(JSON_RESULTS);

				for (int i = 0; i < jsonPlaces.length(); i++) {
					JSONObject place = jsonPlaces.getJSONObject(i);

					String address = place.getString(JSON_ADDRESS);
					String postalCode = place.getString(JSON_POSTAL_CODE);
					String name = place.getString(JSON_NAME);					
					double price = place.getDouble(JSON_PRICE);
					double distance = place.getDouble(JSON_DISTANCE);
					
					result.add(new Place(name, address, postalCode, price, distance));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}
}
