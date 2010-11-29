/*   
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
       
*/
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
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.flurry.android.FlurryAgent;
import com.nagazuka.mobile.android.goedkooptanken.exception.GoedkoopTankenException;
import com.nagazuka.mobile.android.goedkooptanken.exception.LocationException;
import com.nagazuka.mobile.android.goedkooptanken.exception.NetworkException;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.model.PlaceDistanceComparator;
import com.nagazuka.mobile.android.goedkooptanken.model.PlacePriceDistanceComparator;
import com.nagazuka.mobile.android.goedkooptanken.model.PlacesParams;
import com.nagazuka.mobile.android.goedkooptanken.service.DownloadService;
import com.nagazuka.mobile.android.goedkooptanken.service.GeocodingService;
import com.nagazuka.mobile.android.goedkooptanken.service.LocationService;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.AndroidLocationService;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.GoogleHttpGeocodingService;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.ZukaService;
import com.nagazuka.mobile.android.goedkooptanken.util.PlacesUtil;

public class PlacesListActivity extends ListActivity {

	private static final String TAG = PlacesListActivity.class.getName();

	private GoedkoopTankenApp app;
	private PlacesAdapter m_adapter;
	private static PlaceDistanceComparator m_distanceComparator = new PlaceDistanceComparator();
	private static PlacePriceDistanceComparator m_priceDistanceComparator = new PlacePriceDistanceComparator();

	private ProgressDialog m_progressDialog;

	private List<Place> m_places = null;

	private static final int DIALOG_PROGRESS = 1;
	private static final int DIALOG_SEARCH = 2;
	private static final int MAX_PROGRESS = 100;

	private static final int CONTEXT_MENU_MAPS_ID = 0;
	private static final int CONTEXT_MENU_DETAILS_ID = 1;
	private static final int CONTEXT_MENU_NAVIGATION_ID = 2;

	private static final int LOCATION_TASK = 0;
	private static final int DOWNLOAD_TASK = 1;
	private static final int GEOCODE_TASK = 2;
	
	private LocationTask m_locationTask = null;
	private DownloadTask m_downloadTask = null;
	private GeocodeTask m_geocodeTask = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		app = (GoedkoopTankenApp) getApplication();
		m_places = app.getPlaces();
		stopTasks();
		
		if (m_places != null) {
			m_adapter = new PlacesAdapter(this, R.layout.row, m_places);
			setListAdapter(m_adapter);
		} else {			
			ListView listView = getListView();
			listView.setTextFilterEnabled(true);

			m_places = new ArrayList<Place>();
			m_adapter = new PlacesAdapter(this, R.layout.row, m_places);
			setListAdapter(m_adapter);
			startLocationTask();
		}
		registerForContextMenu(getListView());
		
		
	}
	
	@Override
	public void onStart()
	{
	   super.onStart();
	   Resources res = getResources();
	   FlurryAgent.onStartSession(this, res.getString(R.string.flurry_key));
       FlurryAgent.onEvent("Start ListActivity");
	}

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
			break;
		case DIALOG_SEARCH:
			View layout = getLayoutInflater().inflate(R.layout.search_dialog,
					null);

			final EditText edittext = (EditText) layout
					.findViewById(R.id.search_postalcode_text);

			ImageView image = (ImageView) layout
					.findViewById(R.id.ic_search_dialog);
			image.setImageResource(R.drawable.ic_mail);

			DialogInterface.OnClickListener close = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			};

			DialogInterface.OnClickListener search = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String inputString = edittext.getText().toString();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
					if (inputString.length() == 4) {
						app.setPostalCode(inputString);
						new DownloadTask().execute();
					}
					dialog.dismiss();
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(layout);
			builder.setTitle(R.string.search_postalcode);
			builder.setPositiveButton(R.string.error_alert_search_button,
					search);
			builder.setNegativeButton(R.string.error_alert_neg_button, close);
			dialog = builder.create();

			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CONTEXT_MENU_MAPS_ID, 0, "Open in Google Maps");
		menu.add(0, CONTEXT_MENU_NAVIGATION_ID, 1, "Open in Google Navigatie");
		menu.add(0, CONTEXT_MENU_DETAILS_ID, 2, "Bekijk details");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.sort_distance:
			Collections.sort(m_places, m_distanceComparator);
			m_adapter.notifyDataSetChanged();
			return true;
		case R.id.sort_price:
			Collections.sort(m_places, m_priceDistanceComparator);
			m_adapter.notifyDataSetChanged();
			return true;
		case R.id.search_postalcode:
			showDialog(DIALOG_SEARCH);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case CONTEXT_MENU_MAPS_ID:
			openItemInGoogleMaps(info.position);
			return true;
		case CONTEXT_MENU_NAVIGATION_ID:
			openItemInGoogleNavigation(info.position);			
			return true;
		case CONTEXT_MENU_DETAILS_ID:
			showDetailsDialog(info.position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void openItemInGoogleMaps(int position) {
		if (m_places != null) {
			Place selectedItem = m_places.get(position);			
			Intent mapCall = PlacesUtil.getGoogleMapsIntent(selectedItem); 
			startActivity(mapCall);
		}
	}
	
	private void openItemInGoogleNavigation(int position) {
		if (m_places != null) {
			Place selectedItem = m_places.get(position);			
			Intent mapCall = PlacesUtil.getGoogleNavigationIntent(selectedItem); 
			startActivity(mapCall);
		}
	}

	private void showDetailsDialog(int position) {
		if (m_places != null) {
			Place selectedItem = m_places.get(position);
			String title = selectedItem.getName();
			String summary = selectedItem.getSummary();

			DialogInterface.OnClickListener back = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			};

			new AlertDialog.Builder(PlacesListActivity.this).setTitle(title)
					.setMessage(summary).setPositiveButton("OK", back).show();
		}
	}

	private void showExceptionAlert(String message, Exception e, int taskType) {
		Resources res = getResources();
		if (e != null) {
			Log.e(TAG, "<< Exception occurred: " + e + e.getMessage());
		}

		if (!PlacesListActivity.this.isFinishing()) {
			if (e instanceof LocationException) {
				String buttonText = res
						.getString(R.string.error_alert_location_button);
				showRetryAlert(message, taskType,
						Settings.ACTION_LOCATION_SOURCE_SETTINGS, buttonText);
			} else if (e instanceof NetworkException) {
				String buttonText = res
						.getString(R.string.error_alert_network_button);
				showRetryAlert(message, taskType,
						Settings.ACTION_WIRELESS_SETTINGS, buttonText);
			} else {
				showDefaultExceptionAlert(message);
			}
		}
	}

	private void showDefaultExceptionAlert(String message) {

		DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		};

		new AlertDialog.Builder(this).setTitle(R.string.error_alert_title)
				.setMessage(message).setPositiveButton(
						R.string.error_alert_pos_button, positiveListener)
				.show();
	}

	private void showRetryAlert(final String message, final int taskType,
			final String settingsType, final String buttonText) {
		Resources res = getResources();

		DialogInterface.OnClickListener settings = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent intent = new Intent(settingsType);
				startActivity(intent);
			}
		};

		DialogInterface.OnClickListener retry = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				switch (taskType) {
				case LOCATION_TASK:
					startLocationTask();
					break;
				case DOWNLOAD_TASK:
					startDownloadTask();
					break;
				case GEOCODE_TASK:
					startGeocodeTask();
					break;
				default:
					break;
				}
			}
		};

		new AlertDialog.Builder(PlacesListActivity.this).setTitle(
				res.getString(R.string.error_alert_title)).setMessage(message)
				.setNeutralButton(buttonText, settings).setPositiveButton(
						R.string.error_alert_retry_button, retry).show();
	}
	
	private void startLocationTask() {
		stopLocationTask();
		m_locationTask = new LocationTask();
		m_locationTask.execute();
	}
	
	private void startGeocodeTask() {
		stopGeocodeTask();
		m_geocodeTask = new GeocodeTask();
		m_geocodeTask.execute();
	}

	private void startDownloadTask() {
		stopDownloadTask();
		m_downloadTask = new DownloadTask();
		m_downloadTask.execute();
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        stopTasks();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
        stopTasks();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTasks();
    }

	private void stopTasks() {
		stopLocationTask();
		stopGeocodeTask();
		stopDownloadTask();
	}

	private void stopDownloadTask() {
		if (m_downloadTask != null && m_downloadTask.getStatus() != DownloadTask.Status.FINISHED) {
			m_downloadTask.cancel(true);
			m_downloadTask = null;
        }
	}

	private void stopGeocodeTask() {
		if (m_geocodeTask != null && m_geocodeTask.getStatus() != GeocodeTask.Status.FINISHED) {
			m_geocodeTask.cancel(true);
			m_geocodeTask = null;
        }
	}

	private void stopLocationTask() {
		if (m_locationTask != null && m_locationTask.getStatus() != LocationTask.Status.FINISHED) {
        	m_locationTask.cancel(true);
        	m_locationTask = null;
        }
	}

	private class LocationTask extends AsyncTask<Void, Integer, Location> {

		private Exception m_exception = null;
		private LocationManager m_locationManager = null;
		private LocationService m_locationService = null;

		@Override
		public void onPreExecute() {
			m_exception = null;
			m_locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			m_locationService = new AndroidLocationService();

			showDialog(DIALOG_PROGRESS);
			m_progressDialog.setIcon(R.drawable.ic_gps_satellite);
			m_progressDialog.setTitle(R.string.progressdialog_title_location);
			m_progressDialog.setProgress(0);
		}

		@Override
		protected Location doInBackground(Void... params) {
			Location location = null;

			try {
				if (m_locationService == null) {
					throw new GoedkoopTankenException(
							"LocationService is null", null);
				}
				location = m_locationService
						.getCurrentLocation(m_locationManager);

			} catch (Exception e) {
				m_exception = e;
			}

			return location;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			m_progressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Location location) {
			m_progressDialog.setProgress((int) (MAX_PROGRESS * 0.33));

			if (m_exception != null) {
				m_progressDialog.dismiss();
				showExceptionAlert(m_exception.getMessage(), m_exception,
						LOCATION_TASK);
			} else if (location == null) {
				m_progressDialog.dismiss();
				showExceptionAlert(
						"Locatie onbekend, kan tankstations niet downloaden",
						null, LOCATION_TASK);
			} else {
				app.setLocation(location);
				new GeocodeTask().execute();
			}
		}
	}

	private class GeocodeTask extends AsyncTask<Void, Integer, String> {

		private Exception m_exception = null;
		private GeocodingService m_geocodingService = null;
		private int progress = (int) (MAX_PROGRESS * 0.33);
		private static final int MAX_RETRY = 3;		
		
		@Override
		public void onPreExecute() {
			m_exception = null;
			m_geocodingService = new GoogleHttpGeocodingService();

			showDialog(DIALOG_PROGRESS);
			m_progressDialog.setProgress(progress);
			m_progressDialog.setIcon(R.drawable.ic_mail);
			m_progressDialog.setTitle(R.string.progressdialog_title_geocode);
		}

		@Override
		protected String doInBackground(Void... params) {
			String postalCode = "";

			boolean success = false;
			int numRetry = 0;

			while (!success && numRetry < MAX_RETRY) {
				try {
					Location location = app.getLocation();
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();

					// Transform location to address using reverse geocoding
					postalCode = m_geocodingService.getPostalCode(latitude,
							longitude);
					success = true;
					
				} catch (Exception e) {
					m_exception = e;
					Log.e(TAG, "Exception occurred in GeocodeTask ["+ e.getMessage() + "], numRetry ["+ numRetry + "]");
					numRetry++;
					publishProgress(progress + numRetry*3);
				}
			}

			return postalCode;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			m_progressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String postalCode) {
			m_progressDialog.setProgress((int) (MAX_PROGRESS * 0.67));

			if (m_exception != null) {
				m_progressDialog.dismiss();
				showExceptionAlert(m_exception.getMessage(), m_exception,
						GEOCODE_TASK);
			} else if (postalCode == null || postalCode.length() == 0) {
				m_progressDialog.dismiss();
				showExceptionAlert(
						"Postcode onbekend, kan tankstations niet downloaden",
						null, GEOCODE_TASK);
			} else {
				app.setPostalCode(postalCode);
				new DownloadTask().execute();
			}
		}
	}

	private class DownloadTask extends AsyncTask<Void, Integer, List<Place>> {
		private Exception m_exception = null;

		@Override
		protected void onPreExecute() {
			m_exception = null;
			showDialog(DIALOG_PROGRESS);
			m_progressDialog.setProgress((int) (MAX_PROGRESS * 0.67));
			m_progressDialog.setIcon(R.drawable.ic_web);
			m_progressDialog.setTitle(R.string.progressdialog_title_download);
		}

		@Override
		protected List<Place> doInBackground(Void... params) {
			List<Place> results = Collections.emptyList();

			try {
				PlacesParams placesParams = new PlacesParams(app
						.getFuelChoice(), app.getPostalCode());
				DownloadService downloader = new ZukaService();
				results = downloader.fetchPlaces(placesParams);
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
				showExceptionAlert(m_exception.getMessage(), m_exception,
						DOWNLOAD_TASK);
			} else if (result == null || result.size() == 0) {
				m_places.clear();
				m_adapter.notifyDataSetChanged();
				app.setPlaces(m_places);
			} else {
				m_places.clear();
				m_places.addAll(result);
				m_adapter.notifyDataSetChanged();
				app.setPlaces(m_places);
			}
		}
	}
}
