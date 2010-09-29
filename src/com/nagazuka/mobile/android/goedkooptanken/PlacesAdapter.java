package com.nagazuka.mobile.android.goedkooptanken;

import java.util.Collections;
import java.util.List;

import com.nagazuka.mobile.android.goedkooptanken.R;
import com.nagazuka.mobile.android.goedkooptanken.model.Place;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlacesAdapter extends ArrayAdapter<Place> {

	private List<Place> items;

	public List<Place> getItems() {
		return items;
	}

	private void setItems(List<Place> items) {
		Collections.sort(items);
		this.items = items;
	}

	public PlacesAdapter(Context context, int textViewResourceId,
			List<Place> items) {
		super(context, textViewResourceId, items);
		this.setItems(items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row, null);
		}
		Place p = items.get(position);
		if (p != null) {
			TextView nameTextView = (TextView) v.findViewById(R.id.nametext);
			TextView addressTextView = (TextView) v
					.findViewById(R.id.addresstext);
			TextView priceTextView = (TextView) v.findViewById(R.id.pricetext);
			TextView distanceTextView = (TextView) v.findViewById(R.id.distancetext);
			if (nameTextView != null) {
				nameTextView.setText(p.getName());
			}
			if (addressTextView != null) {
				addressTextView.setText(p.getAddress());
			}
			if (priceTextView != null) {
				String priceStr = String.format("\u20AC %.2f", p.getPrice());
				priceTextView.setText(priceStr);
			}
			if (distanceTextView != null) {
				String priceStr = String.format("%.2f km", p.getDistance());
				distanceTextView.setText(priceStr);
			}

		}

		return v;
	}
}