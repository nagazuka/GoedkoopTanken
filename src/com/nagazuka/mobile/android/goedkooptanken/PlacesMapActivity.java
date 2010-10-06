package com.nagazuka.mobile.android.goedkooptanken;

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
import com.nagazuka.mobile.android.goedkooptanken.service.GeocodingService;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.GoogleGeocodingService;

public class PlacesMapActivity extends MapActivity {

	private static final String TAG = PlacesMapActivity.class.getName();
	private MapView mapView;
	
	private GeocodingService m_geocodingService = new GoogleGeocodingService();;
	private List<Overlay> mapOverlays = null;
	private Drawable pinDrawable = null;
	private PlacesItemizedOverlay itemizedoverlay = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();
		GoedkoopTankenApp app = (GoedkoopTankenApp) getApplication();
		// Get current location
		Location currentLocation = app.getLocation();
		List<Place> places = app.getPlaces();

		if (currentLocation != null) {
			double latitude = currentLocation.getLatitude();
			double longitude = currentLocation.getLongitude();

			GeoPoint point = new GeoPoint((int) (latitude * 1E6),
					(int) (longitude * 1E6));

			mapOverlays = mapView.getOverlays();
			pinDrawable = this.getResources().getDrawable(R.drawable.map_pin);
			itemizedoverlay = new PlacesItemizedOverlay(pinDrawable, this);

			OverlayItem overlayitem = new OverlayItem(point, "U bent hier",
					"Dit is uw huidige locatie");

			itemizedoverlay.addOverlay(overlayitem);
			
			mapOverlays.add(itemizedoverlay);
			mc.animateTo(point);

			// Geocode all places and place markers on map
			new GeocodeTask().execute(places);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private class GeocodeTask extends AsyncTask<List<Place>, Place, Void> {

		private Exception m_exception = null;

		@Override
		public void onPreExecute() {
			m_exception = null;
		}

		@Override
		protected Void doInBackground(List<Place>... params) {
			try {
				for (Place p : params[0]) {
					double[] latlong = m_geocodingService.getLocation(p);
					
					double latitude = latlong[0];
					double longitude = latlong[1];
					
					GeoPoint point = new GeoPoint((int) (latitude * 1E6),
							(int) (longitude * 1E6));
					p.setPoint(point);
					
					publishProgress(p);
				}
			} catch (Exception e) {
				Log.e(TAG, "Unexpected exception in GeocodeTask");
				e.printStackTrace();
				m_exception = e;
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Place... progress) {
			Place p = progress[0];
			GeoPoint point = p.getPoint();
			
			OverlayItem overlayitem = new OverlayItem(point, p.getName(),
			p.getSummary());
			itemizedoverlay.addOverlay(overlayitem);			
			mapView.invalidate();
		}

		@Override
		protected void onPostExecute(Void result) {
			mapView.invalidate();
		}
	}

}
