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
import java.util.Random;

public class Controller {
    public static final String TAG="TAG";
    private static Response response;
    public static String[] readData(String artist)
    {
        try
        {
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder().url("http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist="+artist+"&api_key=e1ce6953f9d9982f8fa30f7950f6b072&format=json&autocorrect=1").build();
            response=client.newCall(request).execute();
            String final_json=response.body().string();
            String song_list[]=new String[6];
            int song_list_number=0;
                JSONObject reader,reader1;
                reader = new JSONObject(final_json);
                JSONObject toptracks=reader.getJSONObject("toptracks");
                JSONArray track_array=toptracks.getJSONArray("track");
                for(int i=0;i<3;i++)
                {
                    Random rand=new Random();
                    int xx=rand.nextInt(track_array.length());
                    JSONObject obj=track_array.getJSONObject(xx);
                    String track_name=obj.get("name").toString();
                    JSONObject obj1=obj.getJSONObject("artist");
                    String artist_name=obj1.get("name").toString();
                    OkHttpClient client1=new OkHttpClient();
                    Request request1=new Request.Builder().url("http://ws.audioscrobbler.com/2.0/?method=track.getsimilar&artist="+artist_name+"&track="+track_name+"&api_key=e1ce6953f9d9982f8fa30f7950f6b072&format=json&limit=2&autocorrect=1").build();
                    Response response1 =client1.newCall(request1).execute();
                    String final_json1=response1.body().string();
                    reader1=new JSONObject(final_json1);
                    JSONObject similar_tracks=reader1.getJSONObject("similartracks");
                    JSONArray similar_track_array=similar_tracks.getJSONArray("track");
                    for(int j=0;j<=1;j++)
                    {
                        JSONObject obj_similar_track=similar_track_array.getJSONObject(j);
                        song_list[song_list_number]= (String) obj_similar_track.get("name");
                        song_list_number++;
                    }

                }


            return song_list;

        }
        catch (@NonNull IOException | JSONException e)
        {
            return null;
        }
    }

}
