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
package com.nagazuka.mobile.android.goedkooptanken.model;

import java.util.Comparator;

public class PlacePriceDistanceComparator implements Comparator<Place> {

	@Override
	public int compare(Place p0, Place p1) {
		int priceDiff = (int) (1000 * p0.getPrice())
				- (int) (1000 * p1.getPrice());
		if (priceDiff == 0) {
			return (int) (10 * p0.getDistance())
					- (int) (10 * p1.getDistance());
		} else {
			return priceDiff;
		}
	}
}
