package com.example.darshan.musicai;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Controller {
    public static final String TAG="TAG";
    private static Response response;
    public static JSONObject readData(String artist)
    {
        try
        {
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder().url("http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist="+artist+"&api_key=e1ce6953f9d9982f8fa30f7950f6b072&format=json&limit=1&autocorrect=1").build();
            response=client.newCall(request).execute();
            return new JSONObject(response.body().string());
        }
        catch (@NonNull IOException | JSONException e)
        {
            Log.e(TAG,e.getLocalizedMessage());
        }
        return null;
    }

}
