package com.example.music_and_video;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.music_and_video.custom.VideoInfo;
import com.example.music_and_video.custom.VideoAddItemAdapter;

import java.io.File;
import java.util.ArrayList;

public class AddVideoIntoPlaylistActivity extends Activity {
    TextView txtTitle;
    Button btnSave;
    ImageButton imgBtnBack;

    ListView list;

    ArrayList<VideoInfo> AllVideo;

    VideoAddItemAdapter adapter;
    String title;
    ArrayList<String> idList = new ArrayList<>();

    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video_into_playlist);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        imgBtnBack = (ImageButton) findViewById(R.id.ImgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        list = (ListView) findViewById(R.id.listViewPlayVideo);

        Intent intent = getIntent();
        Bundle myBundle = intent.getExtras();

        idList = myBundle.getStringArrayList("idList");
        title = myBundle.getString("title");

        txtTitle.setText(title);

        loadAllVideo();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idList.addAll(adapter.addId);
                idList.removeAll(adapter.deleteId);

                try {
                    File storagePath = getFilesDir();


                    db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READWRITE);
                    String query = "delete from VIDEOANDPLAYLIST where videoPlaylistName LIKE ? and id LIKE ?";
                    for (int i = 0; i < adapter.deleteId.size(); i++) {
                        Object[] args = {title, adapter.deleteId.get(i)};
                        db.execSQL(query, args);
                    }


                    query = "insert into VIDEOANDPLAYLIST(id, videoPlaylistName) values (?,?)";
                    for (int i = 0; i < adapter.addId.size(); i++) {
                        Object[] args = {adapter.addId.get(i), title};
                        db.execSQL(query, args);
                    }

                    db.close();
                } catch (IllegalStateException e) {}

                finish();

            }
            }
        );
    }

    private void loadAllVideo(){
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        AllVideo = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst()){
            do {
                int index = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                String name = cursor.getString(index);

                index = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                String id = cursor.getString(index);

                index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                String path = cursor.getString(index);

                index = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
                int size = cursor.getInt(index);

                index = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                int length = cursor.getInt(index);

                VideoInfo video = new VideoInfo(id, name, path, size,length);
                AllVideo.add(video);

            }while (cursor.moveToNext());

            adapter = new VideoAddItemAdapter(this,R.layout.video_add_item, AllVideo, idList);
            list.setAdapter(adapter);
        }
    }

}