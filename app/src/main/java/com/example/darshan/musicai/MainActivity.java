package com.example.darshan.musicai;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.NodeList;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl{
    private BottomSheetBehavior mBottomSheetBehavior1;

    ListView listView;
    TextView textView;
    LinearLayout ll;
    ImageButton top_pause,down_pause,next,previous,three_button;
    View view;
    public BottomNavigationView bottomNavigationView;
    MediaMetadataRetriever metadataRetriever;
    ArrayList <String> predicted_song_list,predicted_artist_list;
    public static String[] items,pathitems,item_artist;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;

    //initializing dummy artist name and ratings
    SongPredict songPredict;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songPredict=new SongPredict(getApplicationContext());
        //open database
        try {
            songPredict.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //initialization section
        listView=(ListView)findViewById(R.id.listview);
        top_pause=(ImageButton)findViewById(R.id.imageButton);
        three_button=(ImageButton)findViewById(R.id.three_dots);
        down_pause=(ImageButton)findViewById(R.id.imageButton2);
        next=(ImageButton)findViewById(R.id.imageButton3);
        textView=(TextView)findViewById(R.id.textView);
        previous=(ImageButton)findViewById(R.id.imageButton4);
        ll=(LinearLayout)findViewById(R.id.bottom_navigator);
        predicted_song_list=new ArrayList<String>();
        predicted_artist_list=new ArrayList<String>();
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        metadataRetriever=new MediaMetadataRetriever();

        //permission section
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        //running init predict in method
        if(songPredict.table_empty())
        {
            songPredict.init();
            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
        }

        //initializing json parsing controller
        final Controller controller=new Controller();

        //extracting files from directory ending with .mp3 and .wav
        ArrayList<File> mySongs=null;
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mySongs = findSong(Environment.getExternalStorageDirectory());
        }

        //music path variables
        items = new String[ mySongs.size() ];
        pathitems=new String[mySongs.size()];
        item_artist=new String[mySongs.size()];

        //adding data into string of arrays
        for(int i=0;i<mySongs.size();i++){
            items[i] =
                    mySongs.get(i).getName().replace(".mp3","").replace(".wav","");
            pathitems[i]= mySongs.get(i).getAbsolutePath();
        }

        //using meta_data_retriever to get information about songs
        for (int i=0;i<mySongs.size();i++)
        {
            metadataRetriever.setDataSource(pathitems[i]);
            if(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)!=null) {
                item_artist[i] = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            }
            else
            {
                item_artist[i]="null";
            }
        }

        //inserting all the string of arrays into adapter

        final MyAdapterSong adp=new MyAdapterSong(this,items,item_artist);
        listView.setAdapter(adp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int
                    position, long l) {

            }
        });

        //bottom sheet section
        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    top_pause.setVisibility(View.GONE);
                }
                else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    top_pause.setVisibility(View.VISIBLE);
                }
                else if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        //bottom navigation section
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_item1:
                    {
                        listView.setAdapter(adp);
                        break;
                    }
                    case R.id.action_item2:
                    {
                        if(isNetworkAvailable()) {
                            new predict_task().execute();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Connect To Internet", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                return true;
            }
        });
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }
    //function to search songs form directories
    public ArrayList<File> findSong(File root){
        ArrayList<File> at = new ArrayList<File>();
        File[] files = root.listFiles();
        for(File singleFile : files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                at.addAll(findSong(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3") ||
                        singleFile.getName().endsWith(".wav")){

                    at.add(singleFile);
                }
            }
        }
        return at;
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int i) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public class predict_task extends AsyncTask<Void,Void,Void>
    {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setTitle("Wait");
            dialog.setMessage("breathe in.. out");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList <String> final_artist=songPredict.Predict();

            try
            {
                predicted_song_list.clear();
                predicted_artist_list.clear();
                String[] song_list;
                String[] artist_list;
                String[][] array_list;
                for(int i=0;i<final_artist.size();i++)
                {
                    array_list= Controller.readData(final_artist.get(i));
                    song_list=array_list[0];
                    artist_list=array_list[1];
                    predicted_song_list.addAll(Arrays.asList(song_list));
                    predicted_artist_list.addAll(Arrays.asList(artist_list));
                    Log.i("artist",final_artist.get(i));
                }
            }
            catch (Exception e)
            {
                Log.i("TAG",e.toString());

            }
            return null;
        }
        @Override
        public void onPostExecute(Void result)
        {
            super.onPreExecute();
            MyAdapterPredict myAdapterPredict=new MyAdapterPredict(MainActivity.this,predicted_song_list,predicted_artist_list);
            listView.setAdapter(myAdapterPredict);
            dialog.dismiss();
        }
    }
}

