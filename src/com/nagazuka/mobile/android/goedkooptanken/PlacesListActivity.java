package com.nagazuka.mobile.android.goedkooptanken;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class PlacesListActivity extends ListActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		  setListAdapter(new ArrayAdapter<String>(this,
		          android.R.layout.simple_list_item_1, COUNTRIES));
		  
		  getListView().setTextFilterEnabled(true);
	}
	
	static final String[] COUNTRIES = new String[] {
	    "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
	    "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
	    "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan",
	    "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Wallis and Futuna", "Western Sahara",
	    "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"
	  };
}
