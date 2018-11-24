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
import java.util.Random;

public class Controller {
    public static final String TAG="TAG";
    private static Response response;
    public static String[][] readData(String artist)
    {
        try
        {
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder().url("http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist="+artist+"&api_key=e1ce6953f9d9982f8fa30f7950f6b072&format=json&autocorrect=1").build();
            response=client.newCall(request).execute();
            String final_json=response.body().string();
            String song_list[]=new String[6];
            String artist_list[]=new String[6];
                JSONObject reader;
                reader = new JSONObject(final_json);
                JSONObject toptracks=reader.getJSONObject("toptracks");
                JSONArray track_array=toptracks.getJSONArray("track");
                for(int i=0;i<6;i++)
                {
                    Random rand=new Random();
                    int xx=rand.nextInt(track_array.length());
                    JSONObject obj=track_array.getJSONObject(xx);
                    String track_name=obj.get("name").toString();
                    JSONObject obj1=obj.getJSONObject("artist");
                    String artist_name=obj1.get("name").toString();
                    song_list[i]=track_name;
                    artist_list[i]=artist_name;

                }
                String[][] return_list=new String[2][];
                return_list[0]=song_list;
                return_list[1]=artist_list;
                return  return_list;

        }
        catch (@NonNull IOException | JSONException e)
        {
            for(int i=0;i<e.getStackTrace().length;i++)
            {
                Log.i("method",e.getStackTrace()[i].toString());
            }
            return null;
        }
    }

}
