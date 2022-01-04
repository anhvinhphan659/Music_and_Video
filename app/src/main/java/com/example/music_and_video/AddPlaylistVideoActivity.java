package com.example.music_and_video;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

public class AddPlaylistVideoActivity extends Activity {
    ImageButton btnBackAPV;
    ImageButton ImgBtnAdd;

    LinearLayout LayoutAllVideo;
//    LinearLayout LayoutFavoVideo;

    ImageView ImgAllVideo;
//    ImageView ImgFavoVideo;

    ViewGroup scrollViewgroup;

    SQLiteDatabase db;


    private ArrayList<String> name = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist_video);

        btnBackAPV = (ImageButton) findViewById(R.id.btnBackAPV);
        ImgBtnAdd = (ImageButton) findViewById(R.id.ImgBtnAdd);

        LayoutAllVideo = (LinearLayout) findViewById(R.id.LayoutAllVideo);
//        LayoutFavoVideo = (LinearLayout) findViewById(R.id.LayoutFavoVideo);

        ImgAllVideo = (ImageView) findViewById(R.id.ImgAllVideo);
//        ImgFavoVideo = (ImageView) findViewById(R.id.ImgFavoVideo);

        scrollViewgroup = (ViewGroup) findViewById(R.id.viewGroup);

        try {

            File storagePath = this.getFilesDir();

            db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READONLY);

            Cursor cursor = db.rawQuery("select videoPlaylistName from VIDEOPLAYLIST", null);

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                name.add(cursor.getString(0));
            }

            db.close();

            for (int i =0;i<name.size();i++)
            {
                if(!name.get(i).equals("Favorite videos"))
                {
                    final View singleFrame = getLayoutInflater().inflate(R.layout.playlist_item_video, null);

                    ImageView ImgPlaylist = (ImageView) singleFrame.findViewById(R.id.ImgPlaylist);
                    TextView txtNamePlaylist = (TextView) singleFrame.findViewById(R.id.txtNamePlaylist);

                    txtNamePlaylist.setText(name.get(i));

                    scrollViewgroup.addView(singleFrame);
                }
            }

        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }

        for(int i = 0; i<scrollViewgroup.getChildCount();i++)
        {
            scrollViewgroup.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView txtNamePlaylist = (TextView) v.findViewById(R.id.txtNamePlaylist);
                    detailPlaylist(txtNamePlaylist.getText().toString());
                }
            });
        }

        btnBackAPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImgBtnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addPlayList(Gravity.CENTER);
            }
        });

        LayoutAllVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailPlaylist("All videos");
            }
        });

        ImgAllVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailPlaylist("All videos");
            }
        });

//        LayoutFavoVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                detailPlaylist("Favorite videos");
//            }
//        });
//
//        ImgFavoVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                detailPlaylist("Favorite videos");
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        name = new ArrayList<>();
        scrollViewgroup.removeAllViewsInLayout();

        try {

            File storagePath = this.getFilesDir();

            db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READONLY);

            Cursor cursor = db.rawQuery("select videoPlaylistName from VIDEOPLAYLIST", null);

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                name.add(cursor.getString(0));
            }

            db.close();

            for (int i =0;i<name.size();i++)
            {
                if(!name.get(i).equals("Favorite videos"))
                {
                    final View singleFrame = getLayoutInflater().inflate(R.layout.playlist_item_video, null);

                    ImageView ImgPlaylist = (ImageView) singleFrame.findViewById(R.id.ImgPlaylist);
                    TextView txtNamePlaylist = (TextView) singleFrame.findViewById(R.id.txtNamePlaylist);

                    txtNamePlaylist.setText(name.get(i));

                    scrollViewgroup.addView(singleFrame);
                }
            }

        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }

        for(int i = 0; i<scrollViewgroup.getChildCount();i++)
        {
            scrollViewgroup.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView txtNamePlaylist = (TextView) v.findViewById(R.id.txtNamePlaylist);
                    detailPlaylist(txtNamePlaylist.getText().toString());
                }
            });
        }
    }

    private void addPlayList(int gravity) {
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

        EditText editTxtName = (EditText) dialog.findViewById(R.id.editTxtName);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View singleFrame = getLayoutInflater().inflate(R.layout.playlist_item_video, null);

//                ImageView ImgPlaylist = (ImageView) singleFrame.findViewById(R.id.ImgPlaylist);
                TextView txtNamePlaylist = (TextView) singleFrame.findViewById(R.id.txtNamePlaylist);

                String newName = editTxtName.getText().toString();

                if (name.contains(newName)) {
                    editTxtName.setText("");
                    editTxtName.setHint("'" + newName + "' is exist. Please enter another name.");
                } else {
                    txtNamePlaylist.setText(newName);

                    name.add(newName);

                    scrollViewgroup.addView(singleFrame);
                    try {
                        File storagePath = getFilesDir();
                        db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READWRITE);

                        String insert = "insert into VIDEOPLAYLIST (videoPlaylistName) values (?)";
                        Object[] args = {newName};
                        db.execSQL(insert, args);


                        db.close();

                    } catch (IllegalStateException e) {
                        throw new IllegalStateException("MainActivity must implement callbacks");
                    }

                    singleFrame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            detailPlaylist(newName);
                        }
                    });

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    public void detailPlaylist (String str){
        Intent intent=new Intent(AddPlaylistVideoActivity.this, VideoListActivity.class);
        Bundle myBundle=new Bundle();

        myBundle.putString("title", str );

        intent.putExtras(myBundle);
        startActivityForResult(intent, 1122);
    }
}