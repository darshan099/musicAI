package com.example.darshan.musicai;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterPredict extends ArrayAdapter {

    ArrayList predicted_song;
    public MyAdapterPredict(Context context, ArrayList song_list) {
        super(context, R.layout.listview_layout_song,R.id.song_text, song_list);
        this.predicted_song=song_list;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.listview_layout_song,parent,false);
        TextView txt_predict=(TextView)row.findViewById(R.id.song_text);
        txt_predict.setText((CharSequence) predicted_song.get(position));

        return row;

    }

}
