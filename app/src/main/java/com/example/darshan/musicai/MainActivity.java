package com.example.darshan.musicai;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.ContentObservable;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BottomSheetBehavior mBottomSheetBehavior1;

    public int publicposition;
    ListView listView;
    Boolean mExternalStorageAvailable;
    NodeList nodeList;
    TextView textView;
    ProgressDialog progressDialog;
    LinearLayout ll;
    ImageButton top_pause,down_pause,next,previous;
    View view;
    public BottomNavigationView bottomNavigationView;
    MediaMetadataRetriever metadataRetriever;
    String[] items,pathitems,item_artist;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialization section
        listView=(ListView)findViewById(R.id.listview);
        top_pause=(ImageButton)findViewById(R.id.imageButton);
        down_pause=(ImageButton)findViewById(R.id.imageButton2);
        next=(ImageButton)findViewById(R.id.imageButton3);
        textView=(TextView)findViewById(R.id.textView);
        previous=(ImageButton)findViewById(R.id.imageButton4);
        ll=(LinearLayout)findViewById(R.id.bottom_navigator);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        metadataRetriever=new MediaMetadataRetriever();

        //permission section
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        //initialize media player
        final MediaPlayer mp=new MediaPlayer();

        //extracting files from directory ending with .mp3 and .wav
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

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
        final ArrayAdapter<String> adp = new
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);

        final ArrayAdapter<String> adp_artist = new
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,item_artist);

        listView.setAdapter(adp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int
                    position, long l) {
                publicposition=position;
            }
        });

        //pause button click listener
        down_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.pause();
            }
        });
        //bottom sheet section
        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged( View bottomSheet, int newState) {
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
            public void onSlide(View bottomSheet, float slideOffset) {
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
                        listView.setAdapter(adp_artist);
                        break;
                    }
                }
                return true;
            }
        });

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

}

