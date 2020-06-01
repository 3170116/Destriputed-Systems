package com.myapps.ds;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

public class ArtistsListAdapter extends BaseAdapter {

    private FragmentActivity context; //context
    private String[] artists; //data source of the list adapter

    //public constructor
    public ArtistsListAdapter(FragmentActivity context, String[] artists) {
        this.context = context;
        this.artists = artists;
    }

    @Override
    public int getCount() {
        return artists.length; //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return artists[position]; //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.artists_list_adapter, parent, false);
        }

        // get current item to be displayed
        String currentItem = (String) getItem(position);

        // get the TextView for item name and item description
        TextView artistName = convertView.findViewById(R.id.artistName);

        //sets the text for item name and item description from the current item object
        artistName.setText(currentItem);

        //refresh UI
        notifyDataSetChanged();

        // returns the view for the current row
        return convertView;
    }
}
