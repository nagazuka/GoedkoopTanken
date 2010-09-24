package com.nagazuka.mobile.android.goedkooptanken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.model.PlacesConstants;
import com.nagazuka.mobile.android.goedkooptanken.model.PlacesParams;
import com.nagazuka.mobile.android.goedkooptanken.service.DownloadService;
import com.nagazuka.mobile.android.goedkooptanken.service.GeoCodingService;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.GoogleGeocoder;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.ZukaService;

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

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_PROGRESS:
			m_progressDialog = new ProgressDialog(PlacesListActivity.this);
			m_progressDialog.setIcon(R.drawable.ic_gps_satellite);
			m_progressDialog.setTitle(R.string.progressdialog_title_location);
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

	private class LocationTask extends AsyncTask<Void, Integer, String> {
		private int mProgress = 0;
		private LocationManager m_locationManager = null;
		private GeoCodingService m_geocodingService = null;
		
		@Override
		public void onPreExecute() {
			m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			m_geocodingService = new GoogleGeocoder();
			
			showDialog(DIALOG_PROGRESS);
			m_progressDialog.setTitle(R.string.progressdialog_title_location);
			m_progressDialog.setProgress(mProgress);
		}

		@Override
		protected String doInBackground(Void... params) {
			String postalCode = "";

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			String provider = m_locationManager.getBestProvider(criteria, true);
			Log.d(TAG, "<< bestProvider: " + provider + ">>");

			// Could be that location services are not enabled or not available
			// on device
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
			mProgress = (int) (MAX_PROGRESS * 0.25);
			publishProgress(mProgress);

			// Transform location to address using reverse geocoding
			postalCode = m_geocodingService.getPostalCode(latitude, longitude);

			mProgress = (int) (MAX_PROGRESS * 0.33);
			publishProgress(mProgress);

			for (int i = mProgress; i <= MAX_PROGRESS / 2; i++) {
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
			mProgress = (int) (MAX_PROGRESS * 0.5);
			m_progressDialog.setProgress(mProgress);
			m_postalCode = result;

			Log.d(TAG, "<< LocationTask: mFuelChoice " + m_fuelChoice
					+ " m_postalCode " + m_postalCode + ">>");

			// TODO: Use String resources for text
			m_headerView.setText("Locatie gevonden, postcode: " + m_postalCode);
			new DownloadTask().execute(m_fuelChoice, m_postalCode);
		}
	}

	private class DownloadTask extends AsyncTask<String, Integer, List<Place>> {
		private int mProgress = (int) (MAX_PROGRESS * 0.5);

		@Override
		protected void onPreExecute() {
			m_progressDialog.setTitle(R.string.progressdialog_title_download);
		}

		@Override
		protected List<Place> doInBackground(String... params) {
			PlacesParams placesParams = new PlacesParams(params[0], params[1]);

			mProgress = (int) (MAX_PROGRESS * 0.75);
			publishProgress(mProgress);

			DownloadService downloader = new ZukaService();
			List<Place> results = downloader.fetchPlaces(placesParams);

			mProgress = (int) (MAX_PROGRESS * 0.90);

			publishProgress(mProgress);

			return results;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			m_progressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(List<Place> result) {
			mProgress = MAX_PROGRESS;
			m_progressDialog.setProgress(mProgress);
			m_progressDialog.dismiss();

			Log
					.d(TAG, "<< DownloadTask: result size = " + result.size()
							+ ">>");

			m_places.addAll(result);
			m_adapter.notifyDataSetChanged();
		}
	}
}