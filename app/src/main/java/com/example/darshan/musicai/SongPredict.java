package com.example.darshan.musicai;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.sql.*;

public class SongPredict{

    public static ArrayList<String> artist=new ArrayList<String>();
    public static ArrayList<Float> artist_rating=new ArrayList<Float>();
    public static ArrayList<String> favourite_list=new ArrayList<String>();
    public static final String DATABASE_NAME="database.db";
    public static final int DATABASE_VERSION=3;
    public static SQLiteDatabase db;
    private static Context context;
    public static DatabaseHelper dbhelper;
    public ArrayList<String> unique_artist=new ArrayList<String>();
    public ArrayList<Float> temp_rating=new ArrayList<Float>();
    public ArrayList<Float> unique_artist_rating=new ArrayList<Float>();
    public ArrayList<Integer> artist_frequency=new ArrayList<Integer>();
    public ArrayList<String> final_artist=new ArrayList<String>();

    public SongPredict(Context context1) {
        context=context1;
        dbhelper=new DatabaseHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public SongPredict open() throws SQLException
    {
        db=dbhelper.getReadableDatabase();
        return this;
    }
    public void close()
    {
        db.close();
    }
    public SQLiteDatabase getdatabaseinstance()
    {
        return db;
    }

    public void init()
    {

        try {


            db = dbhelper.getWritableDatabase();
            db.execSQL("INSERT INTO artist(name,rating) VALUES('Taylor Swift',5.0);");
            db = dbhelper.getWritableDatabase();
            db.execSQL("INSERT INTO artist(name,rating) VALUES('Eminem',5.0);");
            db = dbhelper.getWritableDatabase();
            db.execSQL("INSERT INTO artist(name,rating) VALUES('Ed Sheeran',5.0);");
            db = dbhelper.getWritableDatabase();
            db.execSQL("INSERT INTO artist(name,rating) VALUES('Martin Garrix',5.0);");
            db = dbhelper.getWritableDatabase();
            db.execSQL("INSERT INTO artist(name,rating) VALUES('Drake',5.0);");
        }
        catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
    public static void AddArtistRating(String artist_name,float rating)
    {
        if(!artist_name.equalsIgnoreCase("null")) {
            try
            {

                db = dbhelper.getWritableDatabase();
                db.execSQL("INSERT INTO artist(name,rating) VALUES('"+artist_name+"',"+rating+");");
                db=dbhelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM artist",null);
                cursor.moveToFirst();
                String del_artist=cursor.getString(0);
                db=dbhelper.getWritableDatabase();
                db.execSQL("DELETE FROM artist WHERE id='"+del_artist+"';");

            }
            catch (Exception e)
            {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    public boolean table_empty()
    {
        db=dbhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM artist",null);
        if(!cursor.moveToFirst())
        {
            return true;
        }
        return  false;
    }
    public ArrayList Predict()
    {
        unique_artist.clear();
        temp_rating.clear();
        unique_artist_rating.clear();
        artist_frequency.clear();
        final_artist.clear();
        db=dbhelper.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM artist",null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()){
                    unique_artist.add(cursor.getString(1));
                    temp_rating.add(cursor.getFloat(2));
                    Log.i("name",cursor.getString(1));
                    cursor.moveToNext();
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
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

    public void addFavouriteSong(int track_position)
    {
        try {
            if (dataInFavouritesExists(track_position)) {
                Log.i("EXISTS_FAV","exists");
                db = dbhelper.getWritableDatabase();
                db.execSQL("UPDATE favourites SET count=count+1 WHERE position='" + track_position + "';");
            }
            else {
                db = dbhelper.getWritableDatabase();
                db.execSQL("INSERT INTO favourites(position,count) VALUES('" + track_position + "','1');");
                Log.i("FAV_ADD", "added");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public boolean dataInFavouritesExists(int track_position)
    {
        db=dbhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM favourites WHERE position='"+track_position+"';",null);
        if(cursor.getCount()<=0)
        {
            return false;
        }
        return true;
    }

    public ArrayList getFavouriteSongList()
    {
        try {
            db = dbhelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM favourites ORDER BY count DESC;", null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    favourite_list.add(cursor.getString(1));
                    Log.i("name_fav", cursor.getString(1));
                    Log.i("Count_Fav",cursor.getString(2));

                    cursor.moveToNext();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return favourite_list;

    }
    public void main(String args[])
    {

    }
}