package com.example.darshan.musicai;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    public static final String TAG="TAG";
    private static Response response;
    public static String[] readData(String artist)
    {
        try
        {
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder().url("http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist="+artist+"&api_key=e1ce6953f9d9982f8fa30f7950f6b072&format=json&limit=3&autocorrect=1").build();
            response=client.newCall(request).execute();
            String final_json=response.body().string();
            String song_list[]=new String[3];

                JSONObject reader;
                reader = new JSONObject(final_json);
                JSONObject toptracks=reader.getJSONObject("toptracks");
                JSONArray track_array=toptracks.getJSONArray("track");
                for(int i=0;i<track_array.length();i++)
                {
                    JSONObject obj=track_array.getJSONObject(i);
                    song_list[i]= (String) obj.get("name");
                    //System.out.println(obj.get("name"));
                    /*JSONObject obj1=obj.getJSONObject("artist");
                    System.out.println(obj1.get("name"));
                    */
                }

            return song_list;

        }
        catch (@NonNull IOException | JSONException e)
        {
            return null;
        }
    }

}
