package com.example.darshan.musicai;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Rating;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyAdapterSong extends ArrayAdapter{
    String[] song;
    String[] song_artists;
    public MyAdapterSong(@NonNull Context context, String[] rec_song,String[] rec_artist) {
        super(context, R.layout.listview_layout_song,R.id.song_text,rec_song);
        this.song= rec_song;
        this.song_artists= rec_artist;
        Log.i("check length", String.valueOf(song.length));

    }

    @NonNull
    @Override
    public View getView(int position,View convertView, ViewGroup parent)
    {
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row=inflater.inflate(R.layout.listview_layout_song,parent,false);
        TextView txt_predict=(TextView)row.findViewById(R.id.song_text);
        TextView txt_artist=(TextView)row.findViewById(R.id.song_artist);
        final ImageButton three_dots=(ImageButton)row.findViewById(R.id.three_dots);
        txt_predict.setText(song[position]);
        txt_artist.setText(song_artists[position]);
        three_dots.setTag(position);
        three_dots.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                final PopupMenu popup=new PopupMenu(getContext(),three_dots);
                popup.getMenuInflater().inflate(R.layout.popup_menu,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int i=menuItem.getItemId();
                        if(i==R.id.item1)
                        {
                            ShowRating showRating=new ShowRating(getContext(),(int)three_dots.getTag());
                            showRating.show();
                        }

                        return false;
                    }
                });
                popup.show();

            }
        });
        return row;

    }
}
