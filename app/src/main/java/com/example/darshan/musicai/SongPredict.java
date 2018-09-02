package com.example.darshan.musicai;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.sql.*;

public class SongPredict{

    public static ArrayList<String> artist=new ArrayList<String>();
    public static ArrayList<Float> artist_rating=new ArrayList<Float>();
    public static final String DATABASE_NAME="database.db";
    public static final int DATABASE_VERSION=1;
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
                db=dbhelper.getWritableDatabase();
                db.delete("artist","id=1",null);

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
    public void main(String args[])
    {

    }
}