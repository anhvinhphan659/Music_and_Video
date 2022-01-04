package com.example.music_and_video;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.music_and_video.custom.SongInfo;
import com.example.music_and_video.custom.music_item;

import java.io.File;
import java.util.ArrayList;

public class MusicListActivity extends Activity{
    TextView txtTitle;
    ListView list;

    ImageButton imgBtm3Dot;
    ImageButton btnBackML;

    music_item adapter;

    String title;
    ArrayList<SongInfo> AllSong = new ArrayList<>();
    ArrayList<SongInfo> SongList = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<>();

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        list = (ListView) findViewById(R.id.listViewPlayMusic);
        imgBtm3Dot = (ImageButton) findViewById(R.id.ImgBtn3Dot);
        btnBackML = (ImageButton) findViewById(R.id.btnBackML);
        btnBackML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        Bundle myBundle = intent.getExtras();

        title = myBundle.getString("title");

        txtTitle.setText(title);

        if(title.equals("All songs"))
        {
            loadAllSong();
            for (int i =0; i<AllSong.size();i++){
                idList.add(AllSong.get(i).id);
            }

            music_item adapter = new music_item(this,R.layout.music_item,AllSong);
            list.setAdapter(adapter);

            imgBtm3Dot.setClickable(false);
            imgBtm3Dot.setVisibility(View.INVISIBLE);
        }
        else
        {
            try
            {
                File storagePath = this.getFilesDir();

                db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READONLY);
                String query = "select id from SONGANDPLAYLIST where songPlaylistName LIKE ?";
                String[] args = {title};
                Cursor cursor = db.rawQuery(query,args);

                cursor.moveToPosition(-1);

                while (cursor.moveToNext()) {
                    idList.add(cursor.getString(0));
                }
                db.close();
            }
            catch (IllegalStateException e){
                db.close();
            }

            loadSong(idList);

            adapter = new music_item(this,R.layout.music_item,SongList);
            list.setAdapter(adapter);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent senderIntent=new Intent(MusicListActivity.this, PlayMusicActivity.class);
                Bundle senderBundle=new Bundle();

                senderBundle.putStringArrayList("idList", idList);
                senderBundle.putInt("position", position);

                senderIntent.putExtras(senderBundle);
                startActivity(senderIntent);
            }
        });

        imgBtm3Dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPlaylist(Gravity.BOTTOM);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(!title.equals("All songs"))
        {
            idList.clear();
            SongList.clear();
            adapter.notifyDataSetChanged();

            try
            {
                File storagePath = this.getFilesDir();

                db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READONLY);
                String query = "select id from SONGANDPLAYLIST where songPlaylistName LIKE ?";
                String[] args = {title};
                Cursor cursor = db.rawQuery(query,args);

                cursor.moveToPosition(-1);

                while (cursor.moveToNext()) {
                    idList.add(cursor.getString(0));
                }
                db.close();
            }
            catch (IllegalStateException e){
                db.close();
            }

            loadSong(idList);

            adapter = new music_item(this,R.layout.music_item,SongList);
            list.setAdapter(adapter);
        }
    }

    private void loadAllSong(){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);

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
    }

    private void loadSong(ArrayList<String> idList){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);

        if(cursor != null){
            while(cursor.moveToNext()) {
                int temp = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                String id = cursor.getString(temp);

                if(idList.contains(id))
                {
                    temp = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    String name = cursor.getString(temp);

                    temp = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    String singer = cursor.getString(temp);

                    temp = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    String path = cursor.getString(temp);

                    SongInfo song = new SongInfo(id, name, singer, path);
                    SongList.add(song);
                }
            }
        }
    }

    //Hiển thị dialod
    private void editPlaylist(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_playlist);

        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }

        window.setLayout(550, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;

        window.setAttributes(windowAttributes);

        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }

        TextView txtNamePlayList = (TextView) dialog.findViewById(R.id.txtNamePlayList);
        Button btnRename = (Button) dialog.findViewById(R.id.btnRename);
        Button btnAddOrRemoveItem = (Button) dialog.findViewById(R.id.btnAddOrRemoveItem);
        Button btnDelete = (Button) dialog.findViewById(R.id.btnDelete);

        txtNamePlayList.setText(title);
        btnAddOrRemoveItem.setText("Add/Remove songs");

        if(title.equals("Favorite songs")){
            btnRename.setClickable(false);
            btnRename.setVisibility(View.INVISIBLE);

            btnDelete.setClickable(false);
            btnDelete.setVisibility(View.INVISIBLE);
        }

        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename(Gravity.CENTER);
                dialog.dismiss();

            }
        });

        btnAddOrRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent senderIntent=new Intent(MusicListActivity.this, AddMusicIntoPlaylistActivity.class);
                Bundle senderBundle=new Bundle();

                senderBundle.putStringArrayList("idList", idList);
                senderBundle.putString("title", title);

                senderIntent.putExtras(senderBundle);
                startActivityForResult(senderIntent, 1122);

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try{
                   File storagePath = getFilesDir();

                   db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READWRITE);
                   Object args[] = {title};
                   db.execSQL("delete from SONGPLAYLIST where songPlaylistName like ?",args);
                   db.execSQL("delete from SONGANDPLAYLIST where songPlaylistName like ?",args);

                   db.close();
               }
               catch (IllegalStateException e) {
                   throw new IllegalStateException("MainActivity must implement callbacks");
               }

                dialog.dismiss();

                finish();
            }
        });

        dialog.show();
    }

    private void rename(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_playlist);

        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }

        window.setLayout(550, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;

        window.setAttributes(windowAttributes);

        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }

        TextView txtviewName = (TextView) dialog.findViewById(R.id.txtviewName);
        EditText editTxtName = (EditText) dialog.findViewById(R.id.editTxtName);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);

        txtviewName.setText("Rename playlist");
        editTxtName.setHint("New playlist name");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editTxtName.getText().toString();
                try {
                    File storagePath = getFilesDir();

                    db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READWRITE);

                    Cursor cursor = db.rawQuery("select songPlaylistName from SONGPLAYLIST where songPlaylistName like ?", new String[]{newName});

                    if(cursor != null && cursor.getCount() > 0){
                        editTxtName.setText("");
                        editTxtName.setHint("'" + newName + "' is exist. Please enter another name.");
                    }
                    else
                    {
                        Object args[] = {newName, title};

                        db.execSQL("update  SONGPLAYLIST set songPlaylistName = ? where songPlaylistName like ?", args);
                        db.execSQL("update SONGANDPLAYLIST set  songPlaylistName = ? where songPlaylistName like ?", args);

                        txtTitle.setText(newName);
                        dialog.dismiss();

                    }
                    db.close();
                }
                catch (IllegalStateException e) {
                    throw new IllegalStateException("MainActivity must implement callbacks");
                }

            }
        });
        dialog.show();
    }
}
