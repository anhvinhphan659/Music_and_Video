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

import com.example.music_and_video.custom.SongInfo;
import com.example.music_and_video.custom.MusicAddItemAdapter;

import java.io.File;
import java.util.ArrayList;

public class AddMusicIntoPlaylistActivity extends Activity {
    TextView txtTitle;
    Button btnSave;
    ImageButton imgBtnBack;

    ListView list;

    ArrayList<SongInfo> AllSong;

    MusicAddItemAdapter adapter;
    String title;
    ArrayList<String> idList = new ArrayList<>();

    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music_into_playlist);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        imgBtnBack = (ImageButton) findViewById(R.id.ImgBtnBack);
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        list = (ListView) findViewById(R.id.listViewPlayMusic);

        Intent intent = getIntent();
        Bundle myBundle = intent.getExtras();

        idList = myBundle.getStringArrayList("idList");
        title = myBundle.getString("title");

        txtTitle.setText(title);

        loadAllSong();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idList.addAll(adapter.addId);
                idList.removeAll(adapter.deleteId);

                try
                {
                    File storagePath = getFilesDir();

                    db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READWRITE);
                    String query = "delete from SONGANDPLAYLIST where songPlaylistName LIKE ? and id LIKE ?";
                    for (int i =0 ;i<adapter.deleteId.size();i++)
                    {
                        Object[] args = {title, adapter.deleteId.get(i)};
                        db.execSQL(query,args);
                    }


                    query = "insert into SONGANDPLAYLIST(id, songPlaylistName) values (?,?)";
                    for (int i =0 ;i<adapter.addId.size();i++)
                    {
                        Object[] args = {adapter.addId.get(i), title};
                        db.execSQL(query,args);
                    }

                    db.close();
                }
                catch (IllegalStateException e){
                    db.close();
                }

                finish();
            }
        });

    }

    private void loadAllSong(){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);

        AllSong= new ArrayList<>();

        if(cursor != null){
            while(cursor.moveToNext()) {
                int temp = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                String id = cursor.getString(temp);

                temp = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                String name = cursor.getString(temp);

                temp = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                String singer = cursor.getString(temp);

                temp = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                String path = cursor.getString(temp);

                SongInfo song = new SongInfo(id, name, singer, path);
                AllSong.add(song);
            }


        }
        adapter = new MusicAddItemAdapter(this,R.layout.music_add_item,AllSong, idList);
        list.setAdapter(adapter);
    }
}