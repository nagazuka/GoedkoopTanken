package com.nagazuka.mobile.android.goedkooptanken;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class PlacesMapActivity extends MapActivity {

	private MapView mapView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();

		// Get current location
		Location currentLocation = ((GoedkoopTankenApp) getApplication())
				.getLocation();
		if (currentLocation != null) {
			double latitude = currentLocation.getLatitude();
			double longitude = currentLocation.getLongitude();

			GeoPoint point = new GeoPoint((int) (latitude * 1E6),
					(int) (longitude * 1E6));

			
			
			List<Overlay> mapOverlays = mapView.getOverlays();
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.map_pin);
			PlacesItemizedOverlay itemizedoverlay = new PlacesItemizedOverlay(
					drawable, this);

			OverlayItem overlayitem = new OverlayItem(point, "U bent hier",
					"Dit is uw huidige locatie");

			itemizedoverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedoverlay);
			
			mc.animateTo(point);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
