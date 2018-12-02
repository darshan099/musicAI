package com.example.darshan.musicai;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS artist(id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(300), rating FLOAT)");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS favourites(id INTEGER PRIMARY KEY AUTOINCREMENT,position INTEGER, count INTEGER)");
        }
        catch (Exception e)
        {
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS aritst");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS favourites");
        onCreate(sqLiteDatabase);

    }
}
