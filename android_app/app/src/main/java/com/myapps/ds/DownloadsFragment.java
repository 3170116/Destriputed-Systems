package com.myapps.ds;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DownloadsFragment extends Fragment {

    public static final MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.downloads_fragment, container, false);

        String[] songs = loadSongs();
        DownloadsListAdapter downloadsListAdapter = new DownloadsListAdapter(getActivity(),songs);

        final ListView songsList = view.findViewById(R.id.downloadsList);
        songsList.setAdapter(downloadsListAdapter);

        songsList.setClickable(true);
        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String song = (String) songsList.getItemAtPosition(position);

                // resetting mediaplayer instance to evade problems
                mediaPlayer.reset();

                // In case you run into issues with threading consider new instance like:
                // MediaPlayer mediaPlayer = new MediaPlayer();

                // Tried passing path directly, but kept getting
                // "Prepare failed.: status=0x1"
                // so using file descriptor instead
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(new File(getContext().getFilesDir(),song));
                    mediaPlayer.setDataSource(fis.getFD());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
            }
        });

        return view;
    }

    private String[] loadSongs() {
        String[] songs = null;

        File[] songFiles =new File(String.valueOf(getContext().getFilesDir())).listFiles();
        songs = new String[songFiles.length];

        for (int i = 0; i < songFiles.length; i++)
            songs[i] = songFiles[i].getName();

        return songs;
    }
}
