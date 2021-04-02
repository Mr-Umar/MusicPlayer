package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout parentreletivelayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerintent;
    private String keeper = "";

    private ImageView playpausebtn, nextbtn, previousbtn;
    private TextView songnametxtview;

    private ImageView imageView;
    private RelativeLayout lower;
    private Button voiceenable;
    private String mode = "ON";

    private MediaPlayer mymediaplayer;
    private int position;
    private ArrayList<File> mysongs;
    private String songnames;
    SeekBar seekBar;
    Thread updateseekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setTitle("Now Playing");
       // getSupportActionBar().setDisplayShowHomeEnabled(true);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkvoicecommand();

        seekBar = findViewById(R.id.seekbar);
        playpausebtn = findViewById(R.id.btnplaypause);
        nextbtn = findViewById(R.id.btnnext);
        previousbtn = findViewById(R.id.btnprevious);
        imageView = findViewById(R.id.logo);

        lower = findViewById(R.id.lower);
        voiceenable = findViewById(R.id.voicebtn);
        songnametxtview = findViewById(R.id.namesong);


        parentreletivelayout = findViewById(R.id.parentreletivelayout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        validaateandplaysong();



        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matchesfound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesfound != null) {
                    if (mode.equals("ON")) {
                        keeper = matchesfound.get(0);
                        if (keeper.equals("stop")) {
                            playpausesong();
                            Toast.makeText(MainActivity.this, "Command=" + keeper, Toast.LENGTH_LONG).show();
                        } else if (keeper.equals("play")) {
                            playpausesong();
                            Toast.makeText(MainActivity.this, "Command=" + keeper, Toast.LENGTH_LONG).show();
                        } else if (keeper.equals("next")) {
                            nextsong();
                            Toast.makeText(MainActivity.this, "Command=" + keeper, Toast.LENGTH_LONG).show();
                        } else if (keeper.equals("back")) {
                            previousSong();
                            Toast.makeText(MainActivity.this, "Command=" + keeper, Toast.LENGTH_LONG).show();
                        }
                    }


                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        parentreletivelayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerintent);
                        keeper = "";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });


        voiceenable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals("ON")) {
                    mode = "OFF";
                    voiceenable.setText("Voice Enabled Mode  -  OFF");
                    lower.setVisibility(View.VISIBLE);
                } else {
                    mode = "ON";
                    voiceenable.setText("Voice Enabled Mode  -  ON");
                    lower.setVisibility(View.INVISIBLE);
                }

            }
        });


        playpausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playpausesong();

            }
        });


        previousbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mymediaplayer.getCurrentPosition() > 0) {
                    previousSong();
                }


            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mymediaplayer.getCurrentPosition() > 0) {
                    previousSong();
                }

            }
        });

        //seek bar
        updateseekbar =new Thread()
    {
        @Override
        public void run()
        {
            super.run();

            int totalduration=mymediaplayer.getDuration();
            int currentposition=0;
            while (currentposition<totalduration)
            {
                try {
                    sleep(500);
                    currentposition=mymediaplayer.getCurrentPosition();
                    seekBar.setProgress(currentposition);

                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();

                }




            }
        }
    };
        seekBar.setMax(mymediaplayer.getDuration());
        updateseekbar.start();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {   mymediaplayer.seekTo(seekBar.getProgress());

            }
        });
    }


    private void validaateandplaysong() {
        if (mymediaplayer != null) {
            mymediaplayer.stop();
            mymediaplayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mysongs = (ArrayList) bundle.getParcelableArrayList("song");
        songnames = mysongs.get(position).getName();
        String songname = intent.getStringExtra("name");//2activity name
        songnametxtview.setText(songname);
        songnametxtview.setSelected(true);

        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mysongs.get(position).toString());

        mymediaplayer = MediaPlayer.create(MainActivity.this, uri);
        mymediaplayer.start();

    }



    private void checkvoicecommand() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void playpausesong() {

        if (mymediaplayer.isPlaying()) {
            playpausebtn.setImageResource(R.drawable.play);
            mymediaplayer.pause();
        } else {
            playpausebtn.setImageResource(R.drawable.pause);
            mymediaplayer.start();

        }

    }
    private  void play()
    {

    }

    /**
          */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);

    }

    private void nextsong() {

        mymediaplayer.pause();
        mymediaplayer.stop();
        mymediaplayer.release();

        position = ((position + 1) % mysongs.size());

        Uri uri = Uri.parse(mysongs.get(position).toString());

        mymediaplayer = MediaPlayer.create(MainActivity.this, uri);
        songnames = mysongs.get(position).toString();
        songnametxtview.setText(songnames);
        mymediaplayer.start();



    }

    private void previousSong()
    {
        mymediaplayer.pause();
        mymediaplayer.stop();
        mymediaplayer.release();

        position = ((position - 1) < 0 ? (mysongs.size() - 1) : position - 1);

        Uri uri = Uri.parse(mysongs.get(position).toString());

        mymediaplayer = MediaPlayer.create(MainActivity.this, uri);
        songnames = mysongs.get(position).toString();
        songnametxtview.setText(songnames);
        mymediaplayer.start();


    }



}



