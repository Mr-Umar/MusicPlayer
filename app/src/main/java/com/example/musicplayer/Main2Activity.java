package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private String[] itemall;
    private ListView songlist;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        songlist=findViewById(R.id.songlist);

        appexternalstgpermision();
        requestAudioPermissions();
    }

    public  void appexternalstgpermision()
    {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener()
                {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        displayaudiosong();

                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                }).check();

    }
    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            displayaudiosong();

        }
    }

    public ArrayList<File>readAudio(File file)
     {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] allFiles = file.listFiles();


        for (File individualsFiles : allFiles)
          {
            if (individualsFiles.isDirectory() && !individualsFiles.isHidden())
            {
                arrayList.addAll(readAudio(individualsFiles));

            } else
                {
                if (individualsFiles.getName().endsWith(".mp3")|| individualsFiles.getName().endsWith(".aac"))
                {
                    arrayList.add(individualsFiles);
                }
            }
        }
        return arrayList;
    }



    public void displayaudiosong()///////////////////////////////////////////////////////////////
    {
        final ArrayList<File> audiosong = readAudio(Environment.getExternalStorageDirectory());

        itemall=new String[audiosong.size()];
        for (int songcount=0;songcount<audiosong.size();songcount++)
        {
            itemall[songcount]=audiosong.get(songcount).getName();
        }
        ArrayAdapter<String>arrayAdapter=new ArrayAdapter<String>(Main2Activity.this,
                android.R.layout.simple_list_item_1, itemall);
        songlist.setAdapter(arrayAdapter);


        songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                String songname=songlist.getItemAtPosition(position).toString();

                Intent intent= new Intent(Main2Activity.this,MainActivity.class);
                intent.putExtra("song",audiosong);
                intent.putExtra("name",songname);
                intent.putExtra("position",position);
                startActivity(intent);

            }
        });
    }

}
