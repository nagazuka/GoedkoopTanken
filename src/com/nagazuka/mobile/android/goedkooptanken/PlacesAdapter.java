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

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nagazuka.mobile.android.goedkooptanken.model.Place;
import com.nagazuka.mobile.android.goedkooptanken.model.PlacePriceDistanceComparator;

public class PlacesAdapter extends ArrayAdapter<Place> {

	private List<Place> items;
	private LayoutInflater inflater;
	private PlacePriceDistanceComparator comparator = new PlacePriceDistanceComparator();

	public List<Place> getItems() {
		return items;
	}

	private void setItems(List<Place> items) {
		Collections.sort(items, comparator);
		this.items = items;
	}

	public PlacesAdapter(Context context, int textViewResourceId,
			List<Place> items) {
		super(context, textViewResourceId, items);
		this.setItems(items);
        inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row, null);

			holder = new ViewHolder();

			holder.nameTextView = (TextView) convertView
					.findViewById(R.id.nametext);
			holder.addressTextView = (TextView) convertView
					.findViewById(R.id.addresstext);
			holder.townTextView = (TextView) convertView
					.findViewById(R.id.towntext);
			holder.priceTextView = (TextView) convertView
					.findViewById(R.id.pricetext);
			holder.distanceTextView = (TextView) convertView
					.findViewById(R.id.distancetext);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Place p = items.get(position);
		if (p != null) {
			if (holder.nameTextView != null) {
				holder.nameTextView.setText(p.getName());
			}
			if (holder.addressTextView != null) {
				holder.addressTextView.setText(p.getAddress());
			}
			if (holder.townTextView != null) {
				holder.townTextView.setText(p.getTown());
			}
			if (holder.priceTextView != null) {
				String priceStr = String.format("\u20AC %.2f", p.getPrice());
				holder.priceTextView.setText(priceStr);
			}
			if (holder.distanceTextView != null) {
				String priceStr = String.format("%.2f km", p.getDistance());
				holder.distanceTextView.setText(priceStr);
			}
		}

		return convertView;
	}

	static class ViewHolder {
		TextView nameTextView;
		TextView addressTextView;
		TextView townTextView;
		TextView priceTextView;
		TextView distanceTextView;
	}
}