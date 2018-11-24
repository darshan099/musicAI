package com.example.darshan.musicai;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class ShowRating extends Dialog implements android.view.View.OnClickListener{
    public Context c;
    public Dialog d;
    public int position;
    public Button yes,no;
    public RatingBar ratingBar;
    public String[] song_artist=MainActivity.item_artist;


    public ShowRating(@NonNull Context context,int position) {
        super(context);
        this.c=context;
        this.position=position;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rating_dialog);
        ratingBar=(RatingBar)findViewById(R.id.menu_rating_bar);
        yes=(Button)findViewById(R.id.btnyes);
        no=(Button)findViewById(R.id.btnno);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnyes:
            {
                float rating_stars=ratingBar.getRating();
                SongPredict.AddArtistRating(song_artist[position],rating_stars);
                Toast.makeText(getContext(), "Artist: "+song_artist[position]+" rating: "+String.valueOf(rating_stars), Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btnno:
            {
                dismiss();
                break;
            }
            default:
                break;
        }
        dismiss();
    }
}
