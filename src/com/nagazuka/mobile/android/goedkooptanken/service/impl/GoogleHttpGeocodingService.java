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

	private static String constructReverseGeocodingURL(double latitude,
			double longitude) {
		String URL = URL_GOOGLE_API;
		// TODO: region?
		String params = "json?latlng=" + latitude + "," + longitude
				+ "&sensor=true";
		return URL + params;
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

	private String parsePostalCode(String response) {
		String postalCode = "";
		try {
			JSONObject jsonResponse = new JSONObject(response);
			if (jsonResponse.has("status")) {
				String status = jsonResponse.getString("status");
				if (!status.equals("OK")) {
					Log.e(TAG,
							"<< Exception occurred for geocoding, HTTP response: "
									+ response);
					throw new GoedkoopTankenException(
							"Uw postcode kan niet bepaald worden. Onbekende fout opgetreden.",
							null);
				}
			}
			JSONArray results = getResultsArray(jsonResponse);
			postalCode = getPostalCode(results);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return postalCode;
	}

	private String getPostalCode(JSONArray results) throws JSONException {
		for (int i = 0; i < results.length(); i++) {
			JSONObject result = (JSONObject) results.get(i);
			JSONArray addrComps = getAddressComponents(result);
			getPostalCodeFromAddressComponents(addrComps);
		}
		return null;
	}

	private JSONArray getAddressComponents(JSONObject result)
			throws JSONException {
		JSONArray addrComps = null;
		if (result.has("address_components")) {
			addrComps = result.getJSONArray("address_components");
		}
		return addrComps;
	}

	private String getPostalCodeFromAddressComponents(
			JSONArray addressComponents) throws JSONException {
		String postalCode = null;
		for (int i = 0; i < addressComponents.length(); i++) {
			JSONObject addrComp = (JSONObject) addressComponents.get(i);
			if (addrComp.has("types")) {
				JSONArray types = addrComp.getJSONArray("types");
				if (hasPostalCodeType(types)) {
					postalCode = getPostalCodeFromAddressComponent(addrComp);
					break;
				}
			}
		}
		return postalCode;
	}

	private String getPostalCodeFromAddressComponent(JSONObject addrComp)
			throws JSONException {
		String postalCode = null;
		if (addrComp.has("long_name")) {
			postalCode = addrComp.getString("long_name");
		} else if (addrComp.has("short_name")) {
			postalCode = addrComp.getString("short_name");
		}
		return postalCode;
	}

	private boolean hasPostalCodeType(JSONArray types) throws JSONException {
		boolean res = false;
		for (int i = 0; i < types.length(); i++) {
			String type = (String) types.get(i);
			if (type.equals("postal_code")) {
				res = true;
			}

		}
		return res;
	}

	private JSONArray getResultsArray(JSONObject jsonResponse)
			throws JSONException {
		JSONArray results = null;
		if (jsonResponse.has("results")) {
			results = jsonResponse.getJSONArray("results");
		}
		return results;
	}
}