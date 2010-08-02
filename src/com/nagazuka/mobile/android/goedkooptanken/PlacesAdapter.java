package com.nagazuka.mobile.android.goedkooptanken;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlacesAdapter extends ArrayAdapter<Place> {

    private List<Place> items;

    public PlacesAdapter(Context context, int textViewResourceId, List<Place> items) {
            super(context, textViewResourceId, items);
            this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            }
            Place p = items.get(position);
            if (p != null) {
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    if (tt != null) {
                          tt.setText("Name: "+p.getName());                            }
                    if(bt != null){
                          bt.setText("Address: "+ p.getAddress());
                    }
            }
            return v;
    }
}