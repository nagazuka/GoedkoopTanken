package com.nagazuka.mobile.android.goedkooptanken.service.impl;

import java.io.IOException;

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
import com.nagazuka.mobile.android.goedkooptanken.exception.NetworkException;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.service.GeocodingService;

public abstract class GoogleHttpGeocodingService implements GeocodingService {
  /*
	private static final String TAG = GoogleHttpGeocodingService.class
			.getName();
	private static final String URL_GOOGLE_API = "http://maps.googleapis.com/maps/api/geocode/";

	@Override
	public String getPostalCode(double latitude, double longitude)
			throws GoedkoopTankenException {
		String postalCode = "";
		
		String url = constructReverseGeocodingURL(latitude, longitude);
		String jsonResponse = download(url);
		postalCode = parsePostalCode(jsonResponse);
		
		return postalCode;
	}
	
	private static String constructReverseGeocodingURL(double latitude, double longitude) {
		String URL = URL_GOOGLE_API;
		//TODO: region?
		String params = "json?latlng=" + latitude + "," + longitude + "&sensor=true";
		return URL + params;
	}
	
	private static String parsePostalCode(String jsonResponse) throws GoedkoopTankenException {
		String result = "";
		try {
			JSONObject response = new JSONObject(jsonResponse);

			if (response.has("status") && response.getJSONObject("status").equals("OK")) {
				JSONArray results = response.getJSONArray("results");

				for (int i = 0; i < results.length(); i++) {
					JSONObject geoResult = results.getJSONObject(i);
					if (geoResult.has("types")) {
						JSONArray types = geoResult.getJSONArray("types");
						for()
					}
				}
			}	
			else {
					
			}			
		} catch (JSONException e) {
			e.printStackTrace();
			throw new GoedkoopTankenException(
					"Verwerkingsfout opgetreden bij het opvragen de postcode", e);
		}
	
		return result;
	}

	@Override
	public double[] getLocation(Place place) throws GoedkoopTankenException {
		double[] result = { 0.0, 0.0 };

		int maxResults = 1;
		String address = place.getAddress();
		String postalCode = place.getPostalCode();
		String town = place.getTown();
		String locationName = address + ", " + postalCode + ", " + town;

		return result;
	}

	public String download(String url) throws NetworkException {
		String response = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			Log.d(TAG, "<< HTTP Request: " + request.toString());

			ResponseHandler<String> handler = new BasicResponseHandler();
			response = httpClient.execute(request, handler);
			Log.d(TAG, "<< HTTP Response: " + response);

			httpClient.getConnectionManager().shutdown();
		} catch (ClientProtocolException c) {
			c.printStackTrace();
			throw new NetworkException(
					"Er zijn netwerkproblemen opgetreden bij het aanroepen van de geocoder",
					c);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException(
					"Er zijn netwerkproblemen opgetreden bij het aanroepen van de geocoder",
					e);

		}

		return response;
	}
*/
}
