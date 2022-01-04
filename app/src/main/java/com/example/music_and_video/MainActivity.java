package com.example.music_and_video;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.music_and_video.custom.SongInfo;
import com.example.music_and_video.custom.VideoInfo;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {

    ImageButton ImgBtnAboutUs;
    ImageButton ImgBtnMusic;
    ImageButton ImgBtnVideo;
    ImageButton ImgBtnMusicCutter;
    ImageButton ImgBtnVideoCutter;
    ImageButton ImgBtnMusicJoiner;
    ImageButton ImgBtnVideoJoiner;

    LinearLayout LayoutMusic;
    LinearLayout LayoutVideo;
    LinearLayout LayoutMusicCutter;
    LinearLayout LayoutVideoCutter;
    LinearLayout LayoutMusicJoiner;
    LinearLayout LayoutVideoJoiner;

    boolean isPermission = false;

    ArrayList<SongInfo> AllSong = new ArrayList<>();
    ArrayList<VideoInfo> AllVideo = new ArrayList<>();

    SQLiteDatabase db;


    private static ArrayList<String> name = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Xin quyền truy cập
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        try{
            createDatabase();
        }catch (Exception e) {
            finish();
        }

        ImgBtnAboutUs = (ImageButton) findViewById(R.id.imgBtnAboutUs);

        ImgBtnMusic = (ImageButton) findViewById(R.id.ImgBtnMusic);
        ImgBtnVideo = (ImageButton) findViewById(R.id.ImgBtnVideo);

        ImgBtnMusicCutter = (ImageButton) findViewById(R.id.ImgBtnMusicCutter);
        ImgBtnVideoCutter = (ImageButton) findViewById(R.id.ImgBtnVideoCutter);

        ImgBtnMusicJoiner = (ImageButton) findViewById(R.id.ImgBtnMusicJoiner);
        ImgBtnVideoJoiner = (ImageButton) findViewById(R.id.ImgBtnVideoJoiner);

        LayoutMusic = (LinearLayout)findViewById(R.id.LayoutMusic);
        LayoutVideo = (LinearLayout)findViewById(R.id.LayoutVideo);

        LayoutMusicCutter = (LinearLayout)findViewById(R.id.LayoutMusicCutter);
        LayoutVideoCutter = (LinearLayout)findViewById(R.id.LayoutVideoCutter);

        LayoutMusicJoiner = (LinearLayout)findViewById(R.id.LayoutMusicJoiner);
        LayoutVideoJoiner = (LinearLayout)findViewById(R.id.LayoutVideoJoiner);

        ImgBtnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AboutUsActivity.class);
                startActivity(intent);
            }
        });

        ImgBtnMusic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this, AddPlaylistMusicActivity.class);
                startActivity(intent);
            }
        });

        ImgBtnVideo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this, AddPlaylistVideoActivity.class);
                startActivity(intent);
            }
        });

        ImgBtnMusicCutter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,MusicCutterActivity.class);
                startActivity(intent);
            }
        });

        ImgBtnVideoCutter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,VideoCutterActivity.class);
                startActivity(intent);

            }
        });

        ImgBtnMusicJoiner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,MusicJoiner.class);
                startActivity(intent);
            }
        });

        ImgBtnVideoJoiner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,VideoJoiner.class);
                startActivity(intent);

            }
        });

        LayoutMusic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this, AddPlaylistMusicActivity.class);
                startActivity(intent);
            }
        });

        LayoutVideo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this, AddPlaylistVideoActivity.class);
                startActivityForResult(intent, 1122);
            }
        });

        LayoutMusicCutter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


            }
        });

        LayoutVideoCutter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


            }
        });

        LayoutMusicJoiner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,MusicJoiner.class);
                startActivityForResult(intent, 1122);

            }
        });

        LayoutVideoJoiner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent=new Intent(MainActivity.this,VideoJoiner.class);
                startActivityForResult(intent, 1122);

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

        }
        else{
            if(requestCode==1) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }



    public void createDatabase(){
        File storagePath = getApplication().getFilesDir();

        try {
            db = SQLiteDatabase.openOrCreateDatabase(storagePath.getPath()+"/PLAYLIST.db",null);
            String createSongPlaylist="create table if not exists SONGPLAYLIST(" +
                    "songPlaylistName text PRIMARY KEY)";

            String createVideoPlaylist="create table if not exists VIDEOPLAYLIST(" +
                    "videoPlaylistName text PRIMARY KEY)";

            String createSong_Playlist="create table if not exists SONGANDPLAYLIST(" +
                    "id text,"+
                    "songPlaylistName text," +
                    "PRIMARY KEY (id, songPlaylistName))";

            String createVideo_Playlist="create table if not exists VIDEOANDPLAYLIST(" +
                    "id text," +
                    "videoPlaylistName text," +
                    "PRIMARY KEY (id, videoPlaylistName))";

            db.execSQL(createSongPlaylist);
            db.execSQL(createVideoPlaylist);
            db.execSQL(createSong_Playlist);
            db.execSQL(createVideo_Playlist);

            db.execSQL("insert into SONGPLAYLIST(songPlaylistName) values ('Favorite songs')");
            db.execSQL("insert into VIDEOPLAYLIST(videoPlaylistName) values ('Favorite videos')");


            db.setTransactionSuccessful();
        }
        catch (SQLException e){
            db.close();
        }
        finally {
            db.close();
        }
    }
}