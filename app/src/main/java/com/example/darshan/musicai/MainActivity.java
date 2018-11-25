package com.example.darshan.musicai;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private BottomSheetBehavior mBottomSheetBehavior1;

    ListView listView;
    ImageView album_art;
    TextView bottom_song_text;
    LinearLayout ll;
    ImageButton top_pause,down_pause,next,previous,three_button;
    View view;
    public BottomNavigationView bottomNavigationView;
    MediaMetadataRetriever metadataRetriever;
    MediaPlayer player;
    SeekBar seekbar;
    Handler myHandler=new Handler();
    byte[] art;
    int music_position,song_pred_song_toogle=0;
    double starttime=0,finaltime=0;
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
        //-------------------------initialization section----------------------------------
        listView=(ListView)findViewById(R.id.listview);
        top_pause=(ImageButton)findViewById(R.id.imageButton);
        three_button=(ImageButton)findViewById(R.id.three_dots);
        down_pause=(ImageButton)findViewById(R.id.imageButton2);
        next=(ImageButton)findViewById(R.id.imageButton3);
        bottom_song_text=(TextView)findViewById(R.id.textView);
        previous=(ImageButton)findViewById(R.id.imageButton4);
        album_art=(ImageView)findViewById(R.id.imageView);
        ll=(LinearLayout)findViewById(R.id.bottom_navigator);
        seekbar=(SeekBar)findViewById(R.id.seekBar);
        player=new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        predicted_song_list=new ArrayList<String>();
        predicted_artist_list=new ArrayList<String>();
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        metadataRetriever=new MediaMetadataRetriever();
        seekbar.setProgress((int)starttime);
        myHandler.postDelayed(UpdateSongTime,100);


        //-----------------------------permission section--------------------------------------
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        //---------------------------running init predict in method------------------------------
        if(songPredict.table_empty())
        {
            songPredict.init();
            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
        }

        //initializing json parsing controller
        final Controller controller=new Controller();

        //--------------extracting files from directory ending with .mp3 and .wav--------------------
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

        //--------------------------------bottom sheet section-------------------------------------
        final View bottomSheet = findViewById(R.id.design_bottom_sheet);
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

        //----------inserting all the string of arrays into adapter--------------
        final MyAdapterSong adp=new MyAdapterSong(this,items,item_artist);
        listView.setAdapter(adp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int
                    position, long l) {
                if(song_pred_song_toogle==0) {
                    music_position = position;
                    start_song(position);
                }
            }
        });

        /*----------------media player function--------------------------*/
        //pause function
        down_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.isPlaying())
                {
                    player.pause();
                }
                else
                {
                    player.start();
                }
            }
        });

        //pause function
        top_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.isPlaying())
                {
                    player.pause();
                }
                else
                {
                    player.start();
                }
            }
        });

        //next function
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music_position=music_position+1;
                if(music_position > pathitems.length)
                {
                    Toast.makeText(MainActivity.this, "End of Music List!", Toast.LENGTH_SHORT).show();
                    music_position=pathitems.length;
                    start_song(music_position);
                }
                else
                {
                    start_song(music_position);
                }
            }
        });

        //previous function
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music_position=music_position-1;
                if(music_position<0)
                {
                    Toast.makeText(MainActivity.this, "you cant play below 0!", Toast.LENGTH_SHORT).show();
                    music_position=0;
                    start_song(music_position);
                }
                else
                {
                    start_song(music_position);
                }
            }
        });

        //--------------------------seek music--------------------------
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(player!=null && b)
                {
                    player.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //-------------------change to next song when completed-----------------
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                music_position=music_position+1;
                if(music_position > pathitems.length && music_position!=1)
                {
                    Toast.makeText(MainActivity.this, "End of Music List!", Toast.LENGTH_SHORT).show();
                    music_position=pathitems.length;
                    start_song(music_position);
                }
                else if(music_position!=1)
                {
                    start_song(music_position);
                }
                Log.i("comp","music completed");
            }
        });
        //---------------------bottom navigation section-----------------
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_item1:
                    {
                        listView.setAdapter(adp);
                        song_pred_song_toogle=0;
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
//--------------------when back button is pressed---------------------------------
    @Override
    public void onBackPressed()
    {
        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        mBottomSheetBehavior1=BottomSheetBehavior.from(bottomSheet);
        if(mBottomSheetBehavior1.getState()==BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else
        {

        }
    }

    //----------------------------starting music function----------------------------
    public void start_song(int position)
    {
        try {
            player.stop();
            player.reset();
            player.setDataSource(pathitems[position]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();

        MediaMetadataRetriever ret=new MediaMetadataRetriever();
        ret.setDataSource(pathitems[position]);
        try
        {
            art=ret.getEmbeddedPicture();
            Bitmap songimage= BitmapFactory.decodeByteArray(art,0,art.length);
            album_art.setImageBitmap(songimage);
        }
        catch (Exception e)
        {
            album_art.setBackgroundColor(Color.DKGRAY);
        }
        seekbar.setMax(player.getDuration());
        bottom_song_text.setText(items[position]);
        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        mBottomSheetBehavior1=BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
        Log.i("playing",pathitems[position]);
    }

    //----------------------checking network avaailability--------------------------------
    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }
    //--------------------------function to search songs form directories---------------------------
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

    //------------------------------updating song time in seek bar-----------------------------
    private Runnable UpdateSongTime=new Runnable() {
        @Override
        public void run() {
            starttime=player.getCurrentPosition();
            seekbar.setProgress((int)starttime);
            myHandler.postDelayed(this,100);

        }
    };

    //-----------------------------asynchronously get predicted songs------------------------------
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
            song_pred_song_toogle=1;
            dialog.dismiss();
        }
    }
}

