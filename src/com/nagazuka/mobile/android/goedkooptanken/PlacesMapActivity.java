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
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.service.GeocodingService;
import com.nagazuka.mobile.android.goedkooptanken.service.impl.AndroidGeocodingService;
import com.nagazuka.mobile.android.goedkooptanken.util.PlacesUtil;

public class PlacesMapActivity extends MapActivity {

	private static final String TAG = PlacesMapActivity.class.getName();

	private GoedkoopTankenApp app;
	private MapView mapView;

	private GeocodingService m_geocodingService = new AndroidGeocodingService();;
	private List<Overlay> mapOverlays = null;
	private Drawable pinDrawableCheap = null;
	private Drawable pinDrawableExpensive = null;
	private Drawable pinDrawableNormal = null;
	private Drawable userDrawable = null;
	
	private HashMap<Integer,PlacesItemizedOverlay> itemizedOverlays = new HashMap<Integer, PlacesItemizedOverlay>();
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
			pinDrawableCheap = this.getResources().getDrawable(
					R.drawable.map_pin_green);
			pinDrawableNormal = this.getResources().getDrawable(
					R.drawable.map_pin);
			pinDrawableExpensive = this.getResources().getDrawable(
					R.drawable.map_pin_red);

			userDrawable = this.getResources().getDrawable(R.drawable.ic_robot);

			userOverlay = new PlacesItemizedOverlay(userDrawable, this);
			
			itemizedOverlays.put(Place.CHEAP, new PlacesItemizedOverlay(pinDrawableCheap,
					this));
			itemizedOverlays.put(Place.NORMAL, new PlacesItemizedOverlay(pinDrawableNormal,
					this));
			itemizedOverlays.put(Place.EXPENSIVE, new PlacesItemizedOverlay(pinDrawableExpensive,
					this));
			
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
	public void onStart()
	{
	   super.onStart();
	   Resources res = getResources();
	   FlurryAgent.onStartSession(this, res.getString(R.string.flurry_key));
       FlurryAgent.onEvent("Start MapActivity");
	}

	@Override
	public void onStop()
	{
	   super.onStop();
	   FlurryAgent.onEndSession(this);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private class GeocodeTask extends AsyncTask<Void, Place, Void> {

		private HashMap<Integer, Boolean> placedFirstMarker;

		@Override
		public void onPreExecute() {
			placedFirstMarker = new HashMap<Integer, Boolean>();
			placedFirstMarker.put(Place.CHEAP, Boolean.FALSE);
			placedFirstMarker.put(Place.NORMAL, Boolean.FALSE);
			placedFirstMarker.put(Place.EXPENSIVE, Boolean.FALSE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				List<Place> places = app.getPlaces();
				Place.calculatePriceIndicators(places);

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
			
			int priceIndicator = p.getPriceIndicator();
			PlacesItemizedOverlay overlay = itemizedOverlays.get(priceIndicator);			
			if (!placedFirstMarker.get(priceIndicator)) {
				mapOverlays.add(overlay);
				placedFirstMarker.put(priceIndicator, Boolean.TRUE);
			}
			overlay.addOverlay(p);
			mapView.invalidate();
		}

		@Override
		protected void onPostExecute(Void result) {
			mapView.invalidate();
		}
	}

	public class PlacesItemizedOverlay extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		private HashMap<Integer,Place> mPlaces = new HashMap<Integer,Place>();
		
		private Context mContext;

		public PlacesItemizedOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}

		public PlacesItemizedOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			mContext = context;
		}

		@Override
		protected boolean onTap(int index) {
			OverlayItem item = mOverlays.get(index);
			final Place p = mPlaces.get(index);
			DialogInterface.OnClickListener maps = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent mapCall = PlacesUtil.getGoogleMapsIntent(p);
					startActivity(mapCall);
					dialog.dismiss();
				}
			};

			DialogInterface.OnClickListener navigation = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent navCall = PlacesUtil.getGoogleNavigationIntent(p);
					startActivity(navCall);
					dialog.dismiss();
				}
			};

			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(item.getTitle());
			dialog.setMessage(item.getSnippet());
			dialog.setPositiveButton(R.string.maps_button_label, maps);
			dialog.setNegativeButton(R.string.navigation_button_label, navigation);
			dialog.show();
			return true;
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		@Override
		public int size() {
			return mOverlays.size();
		}
		
		public void addOverlay(Place p) {
			GeoPoint point = p.getPoint();
			OverlayItem overlayitem = new OverlayItem(point, p.getName(), p
					.getSummary());
			
			this.addOverlay(overlayitem);			
			int overlayIndex = mOverlays.indexOf(overlayitem);
			mPlaces.put(overlayIndex, p);
		}

		private void addOverlay(OverlayItem overlay) {			
			mOverlays.add(overlay);			
			populate();
		}
	}
}