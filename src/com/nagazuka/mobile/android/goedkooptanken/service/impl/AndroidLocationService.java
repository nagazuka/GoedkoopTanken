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
package com.nagazuka.mobile.android.goedkooptanken.service.impl;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.nagazuka.mobile.android.goedkooptanken.exception.GoedkoopTankenException;
import com.nagazuka.mobile.android.goedkooptanken.exception.LocationException;
import com.nagazuka.mobile.android.goedkooptanken.service.LocationService;

public class AndroidLocationService implements LocationService {
 
	private static final String TAG = AndroidLocationService.class.getName();

	@Override
	public Location getCurrentLocation(LocationManager locationManager) throws GoedkoopTankenException {

		Location location = null;

		try {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			String provider = locationManager.getBestProvider(criteria, true);
			Log.d(TAG, "<< bestProvider: " + provider + ">>");

			// Could be that location services are not enabled or not
			// available
			// on device
			if (provider != null) {
				location = locationManager.getLastKnownLocation(provider);
			} else {
				throw new Exception("Could not get location provider");
			}
		} catch (Exception e) {
			Log.e(TAG,"<< Error find current location with Android Location Service >>");
			e.printStackTrace();
			throw new LocationException("Uw huidige locatie kan niet automatisch bepaald worden",e);
		}

		return location;
	}

}
