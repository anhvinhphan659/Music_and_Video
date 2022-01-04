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

import com.example.music_and_video.custom.VideoInfo;
import com.example.music_and_video.custom.video_item;

import java.io.File;
import java.util.ArrayList;

public class VideoListActivity extends Activity {
    TextView txtTitle;
    ListView list;

    ImageButton imgBtm3Dot;
    ImageButton btnBackVL;

    video_item adapter;

    String title;
    ArrayList<VideoInfo> AllVideo = new ArrayList<>();
    ArrayList<VideoInfo> VideoList = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<>();

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        list = (ListView) findViewById(R.id.listViewPlayVideo);
        imgBtm3Dot = (ImageButton) findViewById(R.id.ImgBtn3Dot);
        btnBackVL = (ImageButton) findViewById(R.id.btnBackVL);
        btnBackVL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        Bundle myBundle = intent.getExtras();

        title = myBundle.getString("title");

        txtTitle.setText(title);


        if(title.equals("All videos"))
        {
            loadAllVideo();

            for (int i = 0; i<AllVideo.size();i++){
                idList.add(AllVideo.get(i).id);
            }

            adapter = new video_item(this,R.layout.video_item,AllVideo);
            list.setAdapter(adapter);

            imgBtm3Dot.setClickable(false);
            imgBtm3Dot.setVisibility(View.INVISIBLE);
        }
        else
        {
            try{
                File storagePath = this.getFilesDir();

                db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READONLY);
                String query = "select id from VIDEOANDPLAYLIST where videoPlaylistName LIKE ?";
                String[] args = {title};
                Cursor cursor = db.rawQuery(query,args);

                cursor.moveToPosition(-1);

                while (cursor.moveToNext()) {
                    idList.add(cursor.getString(0));
                }
                db.close();
            }
            catch (IllegalStateException e){}

            loadVideo(idList);

            adapter = new video_item(this,R.layout.video_item,VideoList);
            list.setAdapter(adapter);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent senderIntent=new Intent(VideoListActivity.this, PlayVideoActivity.class);
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

        if(!title.equals("All videos"))
        {
            idList.clear();
            VideoList.clear();
            adapter.notifyDataSetChanged();

            try
            {
                File storagePath = this.getFilesDir();

                db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READONLY);
                String query = "select id from VIDEOANDPLAYLIST where videoPlaylistName LIKE ?";
                String[] args = {title};
                Cursor cursor = db.rawQuery(query,args);

                cursor.moveToPosition(-1);

                while (cursor.moveToNext()) {
                    idList.add(cursor.getString(0));
                }
                db.close();
            }
            catch (IllegalStateException e){}

            loadVideo(idList);

            adapter = new video_item(this,R.layout.video_item,VideoList);
            list.setAdapter(adapter);
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
        btnAddOrRemoveItem.setText("Add/Remove videos");

        if(title.equals("Favorite videos")){
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
                Intent senderIntent=new Intent(VideoListActivity.this, AddVideoIntoPlaylistActivity.class);
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
                    db.execSQL("delete from VIDEOPLAYLIST where videoPlaylistName like ?",args);
                    db.execSQL("delete from VIDEOANDPLAYLIST where videoPlaylistName like ?",args);

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

                    Cursor cursor = db.rawQuery("select videoPlaylistName from VIDEOPLAYLIST where videoPlaylistName like ?", new String[]{newName});

                    if(cursor != null && cursor.getCount() > 0){
                        editTxtName.setText("");
                        editTxtName.setHint("'" + newName + " is exist. Please enter another name.");
                    }
                    else
                    {
                        Object args[] = {newName, title};

                        db.execSQL("update  VIDEOPLAYLIST set videoPlaylistName = ? where videoPlaylistName like ?", args);
                        db.execSQL("update VIDEOANDPLAYLIST set  videoPlaylistName = ? where videoPlaylistName like ?", args);

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
        }
    }

    private void loadVideo(ArrayList<String> idList) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        VideoList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            while(cursor.moveToNext())
            {
                int index = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                String id = cursor.getString(index);

                if(idList.contains(id)){
                    index = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                    String name = cursor.getString(index);

                    index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    String path = cursor.getString(index);

                    index = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
                    int size = cursor.getInt(index);

                    index = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                    int length = cursor.getInt(index);

                    VideoInfo video = new VideoInfo(id, name, path, size,length);

                    VideoList.add(video);
                }
            }
        }
    }
}