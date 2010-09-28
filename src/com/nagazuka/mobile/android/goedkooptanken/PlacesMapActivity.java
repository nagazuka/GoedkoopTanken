package com.nagazuka.mobile.android.goedkooptanken;

import android.location.Location;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class PlacesMapActivity extends MapActivity {

	private MapView mapView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		/*
		 * List<Overlay> mapOverlays = mapView.getOverlays(); Drawable drawable
		 * = this.getResources().getDrawable( R.drawable.ic_gas_station);
		 * PlacesItemizedOverlay itemizedoverlay = new PlacesItemizedOverlay(
		 * drawable);
		 */

		MapController mc = mapView.getController();

		// Get current location
		Location currentLocation = ((GoedkoopTankenApp) getApplication())
				.getLocation();
		if (currentLocation != null) {
			double latitude = currentLocation.getLatitude();
			double longitude = currentLocation.getLongitude();

			GeoPoint point = new GeoPoint((int) (latitude * 1E6),
					(int) (longitude * 1E6));

			mc.animateTo(point);			
		}

		/*
		 * OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!",
		 * "I'm in Mexico City!");
		 * 
		 * itemizedoverlay.addOverlay(overlayitem);
		 * mapOverlays.add(itemizedoverlay);
		 */
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
