package com.example.darshan.musicai;

import java.util.ArrayList;

public class SongPredict {

    ArrayList<String> artist=new ArrayList<String>();
    ArrayList<Float> artist_rating=new ArrayList<Float>();

    ArrayList<String> unique_artist=new ArrayList<String>();
    ArrayList<Float> temp_rating=new ArrayList<Float>();
    ArrayList<Float> unique_artist_rating=new ArrayList<Float>();
    ArrayList<Integer> artist_frequency=new ArrayList<Integer>();
    ArrayList<String> final_artist=new ArrayList<String>();

    public void init()
    {
        artist.add("Taylor Swift");
        artist.add("Eminem");
        artist.add("Ed Sheeran");
        artist.add("Martin Garrix");
        artist.add("Drake");
        artist_rating.add((float)5);
        artist_rating.add((float)5);
        artist_rating.add((float)5);
        artist_rating.add((float)5);
        artist_rating.add((float)5);
    }

    public void AddArtistRating(String artist_name,float rating)
    {
        artist.add(artist_name);
        artist.remove(0);
        artist_rating.add(rating);
        artist_rating.remove(0);
    }
    public ArrayList Predict()
    {
        unique_artist.clear();
        temp_rating.clear();
        unique_artist_rating.clear();
        artist_frequency.clear();
        final_artist.clear();

        unique_artist=(ArrayList<String>) artist.clone();
        temp_rating=(ArrayList<Float>) artist_rating.clone();
        for(int i=0;i<unique_artist.size();i++)
        {
            int tot_count=1;
            float tot_rating=temp_rating.get(i);
            for (int j=i+1;j<unique_artist.size();j++)
            {
                if(unique_artist.get(i).equalsIgnoreCase(unique_artist.get(j)))
                {
                    unique_artist.remove(j);
                    tot_count=tot_count+1;
                    tot_rating=tot_rating+temp_rating.get(j);
                    temp_rating.remove(j);
                    j=j-1;

                }
            }
            artist_frequency.add(tot_count);
            unique_artist_rating.add((tot_rating/tot_count));
        }

        float final_total_avg=0;
        for(int i=0;i<unique_artist_rating.size();i++)
        {
            final_total_avg=final_total_avg+unique_artist_rating.get(i);
        }
        final_total_avg=(final_total_avg/unique_artist_rating.size());

        for(int i=0;i<unique_artist_rating.size();i++)
        {
            if(unique_artist_rating.get(i)>=final_total_avg)
            {
                final_artist.add(unique_artist.get(i));
            }
        }
        return final_artist;
    }
    public void main(String args[])
    {

    }

}