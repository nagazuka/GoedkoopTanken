package com.nagazuka.mobile.android.goedkooptanken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.model.PlacesConstants;
import com.nagazuka.mobile.android.goedkooptanken.model.PlacesParams;
import com.nagazuka.mobile.android.goedkooptanken.service.DownloadService;
import com.nagazuka.mobile.android.goedkooptanken.service.GeocodingService;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.GoogleGeocodingService;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.ZukaService;

public class PlacesListActivity extends ListActivity {

	private static final String TAG = "PlacesListActivity";

	private PlacesAdapter m_adapter;
	private ProgressDialog m_progressDialog;

	private List<Place> m_places = Collections.emptyList();
	private String m_postalCode = "";
	private String m_fuelChoice = "";

	private static final int DIALOG_PROGRESS = 1;
	private static final int MAX_PROGRESS = 100;

	private static final int CONTEXT_MENU_MAPS_ID = 0;

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

		// Check whether places have been downloaded before
		// to avoid expensive loading on screen orientation change
		final Object data = getLastNonConfigurationInstance();
		final List<Place> downloadedPlaces = (List<Place>) data;

		if (downloadedPlaces != null && downloadedPlaces.size() > 0) {
			m_places = downloadedPlaces;
			m_adapter = new PlacesAdapter(this, R.layout.row, m_places);
			setListAdapter(m_adapter);
		} else {
			m_fuelChoice = getIntent().getStringExtra(
					PlacesConstants.INTENT_EXTRA_FUEL_CHOICE);

			ListView listView = getListView();
			listView.setTextFilterEnabled(true);

			m_places = new ArrayList<Place>();
			m_adapter = new PlacesAdapter(this, R.layout.row, m_places);
			setListAdapter(m_adapter);

			new LocationTask().execute();
		}

		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CONTEXT_MENU_MAPS_ID, 0, "Open in Google Maps");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case CONTEXT_MENU_MAPS_ID:
			openItemInGoogleMaps(info.position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void openItemInGoogleMaps(int position) {
		Log.d(TAG, "<< Position selected [" + position + "]");
		if (m_places != null) {
			Place selectedItem = m_places.get(position);
			Uri geoUri = createGeoURI(selectedItem);
			Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);
			startActivity(mapCall);
		}
	}

	private Uri createGeoURI(Place selectedItem) {
		String geoUriString = "geo:0,0?q=Nederland, ";
		geoUriString += selectedItem.getAddress() + ", "
				+ selectedItem.getPostalCode();
		Log.d(TAG, "<< Geo Uri String [" + geoUriString + "]");
		Uri geoUri = Uri.parse(geoUriString);
		return geoUri;
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// Save downloaded places for future self
		// (e.g. on screen orientation change)
		List<Place> places = null;
		if (m_places != null && m_places.size() > 0) {
			places = m_places;
		} else {
			places = Collections.emptyList();
		}
		return places;
	}

	private void showExceptionAlert(String message, Exception e) {
		Resources res = getResources();
		String exceptionMessage = "";
		if (e != null) {
			Log.e(TAG, "<< Exception occurred in LocationTask: "
					+ e.getMessage());
			exceptionMessage = e.getMessage();
			message += "\n: Details: " + exceptionMessage;
		}

		if (!PlacesListActivity.this.isFinishing()) {
			new AlertDialog.Builder(PlacesListActivity.this)
					.setTitle(res.getString(R.string.error_alert_title))
					.setMessage(message).setPositiveButton(res.getString(R.string.error_alert_pos_button),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									PlacesListActivity.this.finish();
								}})
					.show();
		}
	}

	private class LocationTask extends AsyncTask<Void, Integer, String> {

		private Exception m_exception = null;
		private LocationManager m_locationManager = null;
		private GeocodingService m_geocodingService = null;

		@Override
		public void onPreExecute() {
			m_exception = null;
			m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			m_geocodingService = new GoogleGeocodingService();

			showDialog(DIALOG_PROGRESS);
			m_progressDialog.setTitle(R.string.progressdialog_title_location);
			m_progressDialog.setProgress(0);
		}

		@Override
		protected String doInBackground(Void... params) {
			String postalCode = "";

			try {
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				String provider = m_locationManager.getBestProvider(criteria,
						true);
				Log.d(TAG, "<< bestProvider: " + provider + ">>");

				// Could be that location services are not enabled or not
				// available
				// on device
				if (provider == null) {
					return "";
				}

				Location location = m_locationManager
						.getLastKnownLocation(provider);

				((GoedkoopTankenApp) getApplication()).setLocation(location);

				double latitude = location.getLatitude();
				double longitude = location.getLongitude();

				Log.d(TAG, "<< Latitude: " + latitude + " Longitude: "
						+ longitude + ">>");

				publishProgress((int) (MAX_PROGRESS * 0.25));

				// Transform location to address using reverse geocoding
				postalCode = m_geocodingService.getPostalCode(latitude,
						longitude);

				publishProgress((int) (MAX_PROGRESS * 0.33));

			} catch (Exception e) {
				m_exception = e;
			}

			return postalCode;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			m_progressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			m_progressDialog.setProgress((int) (MAX_PROGRESS * 0.5));
			m_postalCode = result;

			Log.d(TAG, "<< LocationTask: mFuelChoice " + m_fuelChoice
					+ " m_postalCode " + m_postalCode + ">>");

			if (m_exception != null || m_postalCode == null
					|| m_postalCode.length() == 0) {
				m_progressDialog.setProgress(MAX_PROGRESS);
				m_progressDialog.dismiss();
				showExceptionAlert(
						"Locatie kan niet automatisch worden bepaald",
						m_exception);
			} else {
				new DownloadTask().execute(m_fuelChoice, m_postalCode);
			}
		}
	}

	private class DownloadTask extends AsyncTask<String, Integer, List<Place>> {
		private Exception m_exception = null;

		@Override
		protected void onPreExecute() {
			m_exception = null;
			m_progressDialog.setTitle(R.string.progressdialog_title_download);
		}

		@Override
		protected List<Place> doInBackground(String... params) {
			List<Place> results = Collections.emptyList();

			try {
				PlacesParams placesParams = new PlacesParams(params[0],
						params[1]);

				publishProgress((int) (MAX_PROGRESS * 0.75));

				DownloadService downloader = new ZukaService();
				results = downloader.fetchPlaces(placesParams);

				publishProgress((int) (MAX_PROGRESS * 0.90));
			} catch (Exception e) {
				m_exception = e;
			}

			return results;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			m_progressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(List<Place> result) {
			m_progressDialog.setProgress(MAX_PROGRESS);
			m_progressDialog.dismiss();

			if (m_exception != null) {
				showExceptionAlert(
						"Tankstations kunnen niet worden gedownload",
						m_exception);
			} else if (result == null || result.size() == 0) {
				showExceptionAlert("Geen resultaten gevonden", m_exception);
			} else {
				Log.d(TAG, "<< DownloadTask: result size = " + result.size()
						+ ">>");

				m_places.addAll(result);
				m_adapter.notifyDataSetChanged();
			}

		}
	}
}