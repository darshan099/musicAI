package com.example.darshan.musicai;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements music_list.OnFragmentInteractionListener,recommended_list.OnFragmentInteractionListener,favourite_list.OnFragmentInteractionListener, AudioManager.OnAudioFocusChangeListener {


    View view;
    public static MediaPlayer player;
    ImageButton play, pause, play_main, pause_main,next,previous;
    TextView song_title, song_artist_name, startTimeText, endTimeText;
    ImageView song_cover, song_cover_one;
    SeekBar seekBar;
    Toolbar toolbar;
    AudioManager audioManager;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NotificationManager notificationManager;
    Handler myHandler;
    int audio_play_result;
    byte[] art;
    SlidingUpPanelLayout mlayout;
    public static int music_position = 0;
    double starttime = 0;
    public  static ArrayList<String> predicted_song_list, predicted_artist_list;
    public static ArrayList favourite_items;
    public static String[] items, pathitems, item_artist;
    MediaMetadataRetriever metadataRetriever;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;
    //initializing dummy artist name and ratings
    SongPredict songPredict;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songPredict = new SongPredict(getApplicationContext());

        play = findViewById(R.id.play_button);
        pause = findViewById(R.id.pause_button);
        play_main = findViewById(R.id.play_button_main);
        pause_main = findViewById(R.id.pause_button_main);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        song_title = findViewById(R.id.songs_title);
        song_artist_name = findViewById(R.id.songs_artist_name);
        song_cover = findViewById(R.id.song_cover);
        song_cover_one = findViewById(R.id.songs_cover_one);
        startTimeText = findViewById(R.id.startTime);
        endTimeText = findViewById(R.id.endTime);
        seekBar = findViewById(R.id.seekBar3);


        notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        processIntentAction(getIntent());

        toolbar = findViewById(R.id.nav_action);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigation_bar);
        setupDrawerContent(navigationView);

        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audio_play_result=audioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        myHandler = new Handler();

        mlayout = (SlidingUpPanelLayout) findViewById(R.id.activity_main);

        //open database
        try {
            songPredict.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        predicted_song_list = new ArrayList<String>();
        predicted_artist_list = new ArrayList<String>();

        metadataRetriever = new MediaMetadataRetriever();

        final music_list musicList = new music_list();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_layout, musicList);
        transaction.commit();

        seekBar.setProgress((int) starttime);
        myHandler.postDelayed(UpdateSongTime, 100);

        //-----------------------------permission section--------------------------------------
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        //---------------------------running init predict in method------------------------------
        if (songPredict.table_empty()) {
            songPredict.init();
            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
        }

        //initializing json parsing controller
        final Controller controller = new Controller();

        //--------------extracting files from directory ending with .mp3 and .wav--------------------
        ArrayList<File> mySongs = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mySongs = findSong(Environment.getExternalStorageDirectory());
        }

        //music path variables
        items = new String[mySongs.size()];
        pathitems = new String[mySongs.size()];
        item_artist = new String[mySongs.size()];

        //adding data into string of arrays
        for (int i = 0; i < mySongs.size(); i++) {
            items[i] =
                    mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
            pathitems[i] = mySongs.get(i).getAbsolutePath();
        }

        //using meta_data_retriever to get information about songs
        for (int i = 0; i < mySongs.size(); i++) {
            metadataRetriever.setDataSource(pathitems[i]);
            if (metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null) {
                item_artist[i] = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            } else {
                item_artist[i] = "null";
            }
        }


        //-------------------------music options - play/pause etc-------------------//

        //--------play--------------
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) {
                    player.pause();
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                    if (pause_main.getVisibility() == View.VISIBLE) {
                        pause_main.setVisibility(View.GONE);
                        play_main.setVisibility(View.VISIBLE);
                    }
                } else {
                    player.start();
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                    if (play_main.getVisibility() == View.VISIBLE) {
                        play_main.setVisibility(View.GONE);
                        pause_main.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        //-----------pause---------------
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) {
                    player.pause();
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                    if (pause_main.getVisibility() == View.VISIBLE) {
                        pause_main.setVisibility(View.GONE);
                        play_main.setVisibility(View.VISIBLE);
                    }
                } else {
                    player.start();
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                    if (play_main.getVisibility() == View.VISIBLE) {
                        play_main.setVisibility(View.GONE);
                        pause_main.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        //------------play main----------------
        play_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) {
                    player.pause();
                    pause_main.setVisibility(View.GONE);
                    play_main.setVisibility(View.VISIBLE);
                    if (pause.getVisibility() == View.VISIBLE) {
                        pause.setVisibility(View.GONE);
                        play.setVisibility(View.VISIBLE);
                    }
                } else {
                    player.start();
                    play_main.setVisibility(View.GONE);
                    pause_main.setVisibility(View.VISIBLE);
                    if (play.getVisibility() == View.VISIBLE) {
                        play.setVisibility(View.GONE);
                        pause.setVisibility(View.VISIBLE);
                    }

                }

            }
        });

        //-------------pause main----------------------
        pause_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) {
                    player.pause();
                    pause_main.setVisibility(View.GONE);
                    play_main.setVisibility(View.VISIBLE);
                    if (pause.getVisibility() == View.VISIBLE) {
                        pause.setVisibility(View.GONE);
                        play.setVisibility(View.VISIBLE);
                    }
                } else {
                    player.start();
                    play_main.setVisibility(View.GONE);
                    pause_main.setVisibility(View.VISIBLE);
                    if (play.getVisibility() == View.VISIBLE) {
                        play.setVisibility(View.GONE);
                        pause.setVisibility(View.VISIBLE);
                    }

                }

            }
        });

        //-------------next button-----------------------
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music_position=music_position+1;
                if(music_position>items.length)
                {
                    music_position=0;
                    Toast.makeText(MainActivity.this, "End Of Song", Toast.LENGTH_SHORT).show();
                }
                start_song(music_position);
            }
        });

        //---------------previous button-------------
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                music_position=music_position-1;
                if(music_position==-1)
                {
                    music_position=0;
                    Toast.makeText(MainActivity.this, "Cannot Go Above 0!", Toast.LENGTH_SHORT).show();
                }
                start_song(music_position);
            }
        });

        //--------------------------seek music--------------------------
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (MainActivity.player != null && b) {
                    MainActivity.player.seekTo(i);
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
        MainActivity.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                music_position = music_position + 1;
                if (music_position > pathitems.length && music_position != 1) {
                    Toast.makeText(MainActivity.this, "End of Music List!", Toast.LENGTH_SHORT).show();
                    music_position = 0;
                    start_song(music_position);
                } else if (music_position != 1) {
                    start_song(music_position);
                }
                Log.i("comp", "music completed");
            }
        });

        mlayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED))
                {
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.GONE);
                }
                else if(newState.equals(SlidingUpPanelLayout.PanelState.COLLAPSED))
                {
                    if(player.isPlaying())
                    {
                        pause.setVisibility(View.VISIBLE);
                        play.setVisibility(View.GONE);
                    }
                    else
                    {
                        play.setVisibility(View.VISIBLE);
                        pause.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    //--------------------------function to search songs form directories---------------------------
    public ArrayList<File> findSong(File root) {
        ArrayList<File> at = new ArrayList<File>();
        File[] files = root.listFiles();
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                at.addAll(findSong(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") ||
                        singleFile.getName().endsWith(".wav")) {

                    at.add(singleFile);
                }
            }
        }
        return at;
    }

    //--------------override back button press method--------------------//
    @Override
    public void onBackPressed() {
        if (mlayout != null && mlayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mlayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            mlayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    public void getValueFromFragment(int position_from_fragment) {
        start_song(position_from_fragment);
    }

    @SuppressLint("SetTextI18n")
    public void start_song(int track_position) {
        music_position=track_position;
        play_main.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        pause_main.setVisibility(View.VISIBLE);
        pause.setVisibility(View.VISIBLE);
        String position = pathitems[track_position];
        Toast.makeText(this, position, Toast.LENGTH_SHORT).show();
        try {
            player.stop();
            player.reset();
            player.setDataSource(position);

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
        MediaMetadataRetriever ret = new MediaMetadataRetriever();
        ret.setDataSource(position);
        try {
            art = ret.getEmbeddedPicture();
            Bitmap songimage = BitmapFactory.decodeByteArray(art, 0, art.length);
            song_cover.setImageBitmap(songimage);
            song_cover_one.setImageBitmap(songimage);
        } catch (Exception e) {
            song_cover_one.setBackgroundColor(Color.DKGRAY);
            song_cover.setBackgroundColor(Color.DKGRAY);
        }
        seekBar.setMax(MainActivity.player.getDuration());

        song_title.setText(items[track_position]);

        String duration = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long dur = Long.parseLong(duration);

        song_artist_name.setText(item_artist[track_position]);

        String seconds = String.valueOf((dur % 60000) / 1000);
        String minutes = String.valueOf((dur / 60000));
        if (seconds.length() == 1) {
            startTimeText.setText("0" + minutes + ":0" + seconds);
        } else {
            startTimeText.setText("0" + minutes + ":" + seconds);
        }
        songPredict.addFavouriteSong(track_position);
        showActionButtonNotificaton();
        Log.i("playing", items[track_position]);

    }

    //------------------------------updating song time in seek bar-----------------------------
    private Runnable UpdateSongTime = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            starttime = MainActivity.player.getCurrentPosition();
            seekBar.setProgress((int) starttime);
            String seconds = String.valueOf(((int) starttime % 60000) / 1000);
            String minutes = String.valueOf(((int) starttime / 60000));

            if (seconds.length() == 1) {
                startTimeText.setText("0" + minutes + ":0" + seconds);
            } else {
                startTimeText.setText("0" + minutes + ":" + seconds);
            }
            myHandler.postDelayed(this, 100);

        }
    };

    public void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_home: {
                fragmentClass = music_list.class;
                break;
            }
            case R.id.nav_recommended: {
                if(isNetworkAvailable()) {
                    new predict_task().execute();
                }
                fragmentClass=recommended_list.class;
                break;
            }
            case R.id.nav_favourites:
            {
                try {
                    favourite_items.clear();
                    favourite_items = songPredict.getFavouriteSongList();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                fragmentClass=favourite_list.class;
                break;
            }
            default: {
                fragmentClass = music_list.class;
                break;
            }
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit();

        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return false;
    }

    //----------------------checking network avaailability--------------------------------
    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

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
            Class frclass=recommended_list.class;
            Fragment fr= null;
            try {
                fr = (Fragment)frclass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout, fr).commit();
            dialog.dismiss();
        }
    }

    //---------------notification intent---------------
    private Intent getNotificationIntent()
    {
        Intent intent=new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    //-------------show notification------------------------
    public void showActionButtonNotificaton()
    {
        Intent pauseIntent=getNotificationIntent();
        pauseIntent.setAction("PAUSE_ACTION");

        Intent nextIntent=getNotificationIntent();
        nextIntent.setAction("NEXT_ACTION");

        Notification notification=new Notification.Builder(this)
                .setContentIntent(PendingIntent.getActivity(this,0,getNotificationIntent(),PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Notification Received")
                .setContentTitle("Now Playing")
                .setContentText(items[music_position])
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .addAction(new Notification.Action(
                        R.drawable.pause_button,
                        "Pause",
                        PendingIntent.getActivity(this,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                ))
                .addAction(new Notification.Action(
                        R.drawable.forword_button,
                        "Next",
                        PendingIntent.getActivity(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

                ))
                .build();
        notificationManager.notify(100,notification);

    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        processIntentAction(intent);
        super.onNewIntent(intent);
    }
    private void processIntentAction(Intent intent)
    {
        if(intent.getAction()!=null)
        {
            switch (intent.getAction())
            {
                case "PAUSE_ACTION":
                {
                    if(player.isPlaying())
                    {
                        player.pause();
                    }
                    else
                    {
                        player.start();
                    }
                    Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show();
                    break;
                }
                case "NEXT_ACTION":
                {
                    next.performClick();
                    next.setPressed(true);
                    next.invalidate();
                    next.setPressed(false);
                    next.invalidate();

                    Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //--------------audio focus change------------------
    @Override
    public void onAudioFocusChange(int focusChange)
    {
        if(focusChange==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
        {
            pause.performClick();
            pause.setPressed(true);
            pause.invalidate();
            pause.setPressed(false);
            pause.invalidate();
        }
        else if(focusChange==AudioManager.AUDIOFOCUS_GAIN)
        {
            play.performClick();
            play.setPressed(true);
            play.invalidate();
            play.setPressed(false);
            play.invalidate();
        }
        else if(focusChange==AudioManager.AUDIOFOCUS_LOSS)
        {
            pause.performClick();
            pause.setPressed(true);
            pause.invalidate();
            pause.setPressed(false);
            pause.invalidate();
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
