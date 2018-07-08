package com.example.darshan.musicai;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;

public class MyAdapterSong extends ArrayAdapter {
    String[] song;
    public MyAdapterSong(@NonNull Context context, String[] song_list) {
        super(context, R.layout.listview_layout_song,R.id.song_text,song_list);
        this.song=song_list;

    }

    @NonNull
    @Override
    public View getView(int position,View convertView, ViewGroup parent)
    {
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.listview_layout_song,parent,false);
        TextView txt_predict=(TextView)row.findViewById(R.id.song_text);
        final ImageButton three_dots=(ImageButton)row.findViewById(R.id.three_dots);
        txt_predict.setText(song[position]);
        three_dots.setTag(position);

        three_dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),String.valueOf(three_dots.getTag()), Toast.LENGTH_SHORT).show();
            }
        });
        return row;

    }
}
