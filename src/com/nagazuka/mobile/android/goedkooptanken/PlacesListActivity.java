package com.nagazuka.mobile.android.goedkooptanken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListAdapter;

public class PlacesListActivity extends ListActivity {

	private ListAdapter m_adapter;
	private List<Place> m_places;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		m_places = getDummyPlaces();
		
		m_adapter = new PlacesAdapter(this, R.layout.row, m_places);
		setListAdapter(m_adapter);
		//setListAdapter(new ArrayAdapter<String>(this,
		//		android.R.layout.simple_list_item_1, COUNTRIES));

		getListView().setTextFilterEnabled(true);
	}
	
	private static List<Place> getDummyPlaces() {
		List<Place> res = new ArrayList<Place>();
		
		for (String c: GAS_STATIONS) {
			res.add(new Place(c,"Main Street 123",1.43));
		}
		
		return res;
	}

	static final String[] GAS_STATIONS = new String[] { "Shell Binckhorst", "BP",
			"Firezone", "Total", "Elf", "Fina", "Texaco" };
}
