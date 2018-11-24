package com.example.darshan.musicai;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterPredict extends ArrayAdapter {

    String[] predicted_song;
    String[] predicted_artist;
    public MyAdapterPredict(Context context, ArrayList<String> rec_predicted_song, ArrayList<String> rec_predicted_artist) {
        super(context, R.layout.listview_layout_song,R.id.song_text,rec_predicted_song);
        this.predicted_song= rec_predicted_song.toArray(new String[0]);
        this.predicted_artist= rec_predicted_artist.toArray(new String[0]);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.listview_layout_song,parent,false);
        TextView txt_predict=(TextView)row.findViewById(R.id.song_text);
        TextView txt_predict_artist=(TextView)row.findViewById(R.id.song_artist);
        txt_predict.setText(predicted_song[position]);
        txt_predict_artist.setText(predicted_artist[position]);

        return row;

    }

}
