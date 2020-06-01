package com.myapps.ds;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

public class SongsAdapter extends BaseAdapter {

    private FragmentActivity context; //context
    private String[] songs; //data source of the list adapter
    private String artist;

    //public constructor
    public SongsAdapter(FragmentActivity context, String[] songs) {
        this.context = context;
        this.songs = songs;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public int getCount() {
        return songs.length; //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return songs[position]; //returns list item at the specified position
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
                    inflate(R.layout.songs_list_adapter, parent, false);
        }

        // get current item to be displayed
        String currentItem = (String) getItem(position);

        // get the TextView for item name and item description
        TextView songName = convertView.findViewById(R.id.songName);

        //sets the text for item name and item description from the current item object
        songName.setText(currentItem.substring(0,currentItem.length() - 4));

        //refresh UI
        notifyDataSetChanged();

        // returns the view for the current row
        return convertView;
    }
}
