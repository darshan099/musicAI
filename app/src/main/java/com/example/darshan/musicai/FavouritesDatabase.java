package com.example.darshan.musicai;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

public class FavouritesDatabase {
    public static final String DATABASE_NAME="database.db";
    public static final int DATABASE_VERSION=1;
    public static SQLiteDatabase db;
    private static Context context;
    public static DatabaseHelper dbhelper;

    public FavouritesDatabase(Context context1) {
        context=context1;
        dbhelper=new DatabaseHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    public FavouritesDatabase open() throws SQLException
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

    public void addFavouriteSong(int track_position)
    {

    }
}
