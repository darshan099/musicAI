package com.example.darshan.musicai;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.darshan.musicai.MainActivity.item_artist;
import static com.example.darshan.musicai.MainActivity.items;

public class MyAdapterFavourites extends ArrayAdapter{

    String[] favourite_song;
    public MyAdapterFavourites(Context context, ArrayList<String> favourite_song) {
        super(context,R.layout.listview_layout_song,R.id.song_text,favourite_song);
        this.favourite_song=favourite_song.toArray(new String[0]);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.listview_layout_song, parent, false);
        try {

            TextView txt_favourite = (TextView) row.findViewById(R.id.song_text);
            TextView txt_favourite_artist = (TextView) row.findViewById(R.id.song_artist);
            int pos=Integer.parseInt(favourite_song[position]);
            txt_favourite.setText(items[pos]);
            txt_favourite_artist.setText(item_artist[pos]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return row;
    }
}
