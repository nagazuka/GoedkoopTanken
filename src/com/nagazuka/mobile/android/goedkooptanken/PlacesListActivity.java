package com.nagazuka.mobile.android.goedkooptanken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListAdapter;

public class PlacesListActivity extends ListActivity {
	private ListAdapter m_adapter;
	private List<Place> m_places = Collections.emptyList();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m_places = getDummyPlaces();

		m_adapter = new PlacesAdapter(this, R.layout.row, m_places);
		setListAdapter(m_adapter);

		getListView().setTextFilterEnabled(true);
	}

	private static List<Place> getDummyPlaces() {
		List<Place> res = new ArrayList<Place>();
		Random generator = new Random();
		for (int i = 0; i < GAS_STATIONS.length; i++) {
			double price = 1.40 + 0.5 * generator.nextDouble();
			res.add(new Place(GAS_STATIONS[i], ADDRESSES[i], price));
		}		
		return res;
	}

	private static final String[] GAS_STATIONS = new String[] {
			"Shell Binckhorst", "BP", "Firezone", "Total", "Elf", "Fina",
			"Texaco" };

	private static final String[] ADDRESSES = new String[] {
			"Caan van Necklaan 118, Rijswijk ZH",
			"Vsn Campenvaart 49, Den Haag", "Laakweg 214, Den Haag",
			"Wolmaransstraat 331, Den Haag", "Beechavenue 1, Rozenburg",
			"Stadhuisplein 1, Rotterdam", "Coolsingel 10, Rotterdam" };
}