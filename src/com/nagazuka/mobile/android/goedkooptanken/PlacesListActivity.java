package com.nagazuka.mobile.android.goedkooptanken;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class PlacesListActivity extends ListActivity {

	private static final String TAG = "PlacesListActivity";

	private PlacesAdapter m_adapter;
	private ProgressDialog m_progressDialog;
	private TextView m_headerView;

	private List<Place> m_places = Collections.emptyList();
	private String m_postalCode = "";
	private String m_fuelChoice = "";

	private static final int DIALOG_PROGRESS = 1;
	private static final int MAX_PROGRESS = 100;
	private static final String[] GAS_STATIONS = new String[] {
			"Tango Den Haag", "ANWB Tankstation", "Argos Rijswijk",
			"Shell Express Delftselaan", "BP Express Voorburg",
			"Shell Express 's Gravenhage", "Berkman Voorburg" };
	private static final String[] ADDRESSES = new String[] {
			"Waldorpstraat 51, Den Haag",
			"Laan van Nieuw Oost Einde 293, Voorburg",
			"Jan Thijssenweg 14, Den Haag", "Delftselaan 126, Den Haag",
			"Prins Bernhardlaan 516, Voorburg", "Else Mauhslaan 2, Den Haag",
			"Oosteinde 214, Voorburg" };

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_PROGRESS:
			m_progressDialog = new ProgressDialog(PlacesListActivity.this);
			m_progressDialog.setIcon(R.drawable.ic_gps_satellite);
			m_progressDialog.setTitle(R.string.progressdialog_title);
			m_progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			m_progressDialog.setMax(MAX_PROGRESS);
			m_progressDialog.setButton2(
					getText(R.string.progressdialog_cancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							/* User clicked No so do some stuff */
						}
					});
			dialog = m_progressDialog;
		}
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m_fuelChoice = getIntent().getStringExtra(
				PlacesConstants.INTENT_EXTRA_FUEL_CHOICE);

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

		m_headerView = new TextView(getApplicationContext());
		m_headerView.setText("Zoeken naar tankstations voor brandstof "
				+ m_fuelChoice + "...");

		listView.addHeaderView(m_headerView, null, false);

		m_places = new ArrayList<Place>();
		m_adapter = new PlacesAdapter(this, R.layout.row, m_places);

		setListAdapter(m_adapter);

		new LocationTask().execute();
	}

	private static List<Place> getDummyPlaces() {
		ArrayList<Place> res = new ArrayList<Place>();
		Random generator = new Random();
		for (int i = 0; i < GAS_STATIONS.length; i++) {
			double price = 1.40 + 0.5 * generator.nextDouble();
			res.add(new Place(GAS_STATIONS[i], ADDRESSES[i], price));
		}
		Collections.sort(res);
		return res;
	}

	private class LocationTask extends AsyncTask<Void, Integer, String> {
		private int mProgress = 0;
		private LocationManager m_locationManager;

		@Override
		public void onPreExecute() {
			m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			showDialog(DIALOG_PROGRESS);
			m_progressDialog.setProgress(mProgress);
		}

		@Override
		protected String doInBackground(Void... params) {
			String postalCode = "";

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			String provider = m_locationManager.getBestProvider(criteria, true);
			Log.d(TAG, "<< bestProvider: " + provider + ">>");

			// Could be that GPS is not enabled or not available on device
			if (provider == null) {
				return "";
			}

			Location location = m_locationManager
					.getLastKnownLocation(provider);

			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			int maxResults = 1;

			Log.d(TAG, "<< Latitude: " + latitude + " Longitude: " + longitude
					+ ">>");
			mProgress = MAX_PROGRESS / 2;
			publishProgress(mProgress);

			Context context = PlacesListActivity.this.getApplicationContext();

			// Transform location to address using reverse geocoding
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			List<Address> adresses = Collections.emptyList();
			try {
				adresses = geocoder.getFromLocation(latitude, longitude,
						maxResults);
			} catch (IOException e) {
				Log.e(TAG, "<< Error looking up address with Geocoder >>");
				e.printStackTrace();
			}

			if (!adresses.isEmpty()) {
				Address address = adresses.get(0);
				postalCode = address.getPostalCode();
				Log
						.d(TAG, "<< Geocoder found postalCode: " + postalCode
								+ ">>");
			}

			mProgress = MAX_PROGRESS / 3 * 4;
			publishProgress(mProgress);

			for (int i = mProgress; i <= MAX_PROGRESS; i++) {
				mProgress++;
				publishProgress(mProgress);
			}

			return postalCode;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			m_progressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			mProgress = MAX_PROGRESS;
			m_progressDialog.setProgress(mProgress);
			m_progressDialog.dismiss();

			m_postalCode = result;

			Log.d(TAG, "<< LocationTask: mFuelChoice " + m_fuelChoice
					+ " m_postalCode " + m_postalCode + ">>");

			// TODO: Use String resources for text
			m_headerView.setText("Locatie gevonden, postcode: " + m_postalCode);
			new DownloadTask().execute(m_fuelChoice, m_postalCode);
		}
	}

	private class DownloadTask extends AsyncTask<String, Integer, List<Place>> {

		@Override
		protected List<Place> doInBackground(String... params) {
			String result = "";
			
			HttpClient httpClient = new DefaultHttpClient();
			String URL = "http://zukaservice.appspot.com/goedkooptanken/1.0/";
			String combinedParams = "?brandstof=" + URLEncoder.encode(params[0]) + "&postcode=" + params[1];			
			HttpGet request = new HttpGet(URL + combinedParams);
			
			Log.i(TAG, request.toString());
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			try {
				result = httpClient.execute(request, handler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			httpClient.getConnectionManager().shutdown();
			Log.i(TAG, result);

			List<Place> results = getDummyPlaces();
			return results;
		}

		@Override
		protected void onPostExecute(List<Place> result) {
			Log
					.d(TAG, "<< DownloadTask: result size = " + result.size()
							+ ">>");

			m_places.addAll(result);
			m_adapter.notifyDataSetChanged();
		}
	}

}