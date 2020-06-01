package com.myapps.ds;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

public class ArtistsFragment extends Fragment {

    public static Switch aSwitch;
    public static Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.artists_fragment, container, false);
        activity = getActivity();

        aSwitch = view.findViewById(R.id.saveIt);

        String[] artists = MainActivity.consumer.getArtists();
        ArtistsListAdapter downloadsListAdapter = new ArtistsListAdapter(getActivity(),artists);

        final ListView artistsList = view.findViewById(R.id.artistsList);
        artistsList.setAdapter(downloadsListAdapter);

        final ListView songsList = view.findViewById(R.id.songsList);
        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                MainActivity.consumer.push( new TrackName((String) songsList.getItemAtPosition(position),((SongsAdapter) songsList.getAdapter()).getArtist(),aSwitch.isChecked()));
            }
        });

        artistsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String artist = (String) artistsList.getItemAtPosition(position);

                songsList.setAdapter(new SongsAdapter(getActivity(),MainActivity.consumer.getSongOfArtist(artist)));
                ((SongsAdapter) songsList.getAdapter()).setArtist(artist);
            }
        });

        return view;
    }

}
