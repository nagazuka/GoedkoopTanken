package com.nagazuka.mobile.android.goedkooptanken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.model.PlaceDistanceComparator;
import com.nagazuka.mobile.android.goedkooptanken.service.GeocodingService;
import com.nagazuka.mobile.android.goedkooptanken.service.UploadService;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.GoogleGeocodingService;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.ZukaService;

public class PlacesMapActivity extends MapActivity {

	private static final String TAG = PlacesMapActivity.class.getName();

	private GoedkoopTankenApp app;
	private MapView mapView;

	private GeocodingService m_geocodingService = new GoogleGeocodingService();;
	private List<Overlay> mapOverlays = null;
	private Drawable pinDrawable = null;
	private Drawable userDrawable = null;
	private PlacesItemizedOverlay itemizedoverlay = null;
	private PlacesItemizedOverlay userOverlay = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();
		app = (GoedkoopTankenApp) getApplication();
		// Get current location
		Location currentLocation = app.getLocation();

		if (currentLocation != null) {
			double latitude = currentLocation.getLatitude();
			double longitude = currentLocation.getLongitude();

			GeoPoint point = new GeoPoint((int) (latitude * 1E6),
					(int) (longitude * 1E6));

			mapOverlays = mapView.getOverlays();
			pinDrawable = this.getResources().getDrawable(R.drawable.map_pin);
			userDrawable = this.getResources().getDrawable(R.drawable.ic_robot);

			userOverlay = new PlacesItemizedOverlay(userDrawable, this);
			itemizedoverlay = new PlacesItemizedOverlay(pinDrawable, this);

			String currentLocationTitle = getResources().getString(
					R.string.current_location_title);
			String currentLocationText = getResources().getString(
					R.string.current_location_text);
			OverlayItem overlayitem = new OverlayItem(point,
					currentLocationTitle, currentLocationText);

			userOverlay.addOverlay(overlayitem);
			mapOverlays.add(userOverlay);

			mc.setZoom(13);
			mc.animateTo(point);

			// Geocode all places and place markers on map
			new GeocodeTask().execute();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private class GeocodeTask extends AsyncTask<Void, Place, Void> {
		private boolean placedFirstMarker = false;

		@Override
		public void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void...params) {
			try {
				List<Place> places = app.getPlaces();
				Collections.sort(places, new PlaceDistanceComparator());
				for (Place p : places) {
					if (p.getPoint() == null) {
						double[] latlong = m_geocodingService.getLocation(p);

						double latitude = latlong[0];
						double longitude = latlong[1];

						GeoPoint point = new GeoPoint((int) (latitude * 1E6),
								(int) (longitude * 1E6));
						p.setPoint(point);
					}
					publishProgress(p);
				}
			} catch (Exception e) {
				Log.e(TAG, "Unexpected exception in GeocodeTask"
						+ e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Place... progress) {
			Place p = progress[0];
			GeoPoint point = p.getPoint();

			OverlayItem overlayitem = new OverlayItem(point, p.getName(), p
					.getSummary());

			if (!placedFirstMarker) {
				mapOverlays.add(itemizedoverlay);
			}

			itemizedoverlay.addOverlay(overlayitem);
			mapView.invalidate();
		}

		@Override
		protected void onPostExecute(Void result) {
			mapView.invalidate();
			new UploadTask().execute();
		}
	}

	private class UploadTask extends AsyncTask<Void, Place, Void> {

		private UploadService m_uploadService = null;

		@Override
		public void onPreExecute() {
			m_uploadService = new ZukaService();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<Place> places = app.getPlaces();
				List<Place> uploadPlaces = new ArrayList<Place>();
				for (Place p : places) {
					if (p.getPoint() != null) {
						uploadPlaces.add(p);
					}
				}
				m_uploadService.uploadPlaces(uploadPlaces);
			} catch (Exception e) {
				Log.e(TAG, "Unexpected exception in UploadTask"
						+ e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Place... progress) {
		}

		@Override
		protected void onPostExecute(Void result) {
		}
	}
}
