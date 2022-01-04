package com.example.music_and_video;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.music_and_video.custom.SongInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PlayMusicActivity extends Activity {
    TextView txtTitle;

    ImageView imgCD;

    TextView txtCurrentTime;
    TextView txtTotalTime;
    SeekBar sbTime;

    ImageButton btnPrev;
    ImageButton btnPlay;
    ImageButton btnStop;
    ImageButton btnNext;
    ImageButton btnBackPM;
    ImageButton btnFavorite;
    ImageButton btnRepeat;
    ImageButton btnSpeaker;
    ImageButton btnSpeedControl;

    boolean isRepeated = false;
    boolean isMuted = false;
    boolean isFavorite = false;
    float speed = 1;
    int spinnerSelected = 3;

    ArrayList<SongInfo> songList;
    int position;

    ArrayList<String> idList = new ArrayList<>();
    ArrayList<String> idFavoriteList = new ArrayList<>();
    ArrayList<String> newIdFavoriteList = new ArrayList<>();
    ArrayList<String> idUnFavoriteList = new ArrayList<>();



    static MediaPlayer mediaPlayer = null;

    Animation animation;

    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        AnhXa();

        animation = AnimationUtils.loadAnimation(this, R.anim.cd_rotate);

        Intent receiveIntent = getIntent();
        Bundle receiveBundle = receiveIntent.getExtras();

        idList = receiveBundle.getStringArrayList("idList");
        position =receiveBundle.getInt("position");

        loadSong(idList);

        InitMediaPlayer(position);
        mediaPlayer.start();
        btnPlay.setImageResource(R.drawable.music_play_pause_icon);
        imgCD.clearAnimation();
        imgCD.startAnimation(animation);

        SetTotalTime();
        UpdateCurrentTime();

        //Kiểm tra có phải là favotite song
        try {
            File storagePath = this.getFilesDir();

            db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READONLY);

            Cursor cursor = db.rawQuery("select id from SONGANDPLAYLIST where songPlaylistName like 'Favorite songs'", null);

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                idFavoriteList.add(cursor.getString(0));
            }

            db.close();

            if(idFavoriteList.contains(songList.get(position).id))
            {
                isFavorite = true;
                btnFavorite.setImageResource(R.drawable.favorited);
            }

        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position > songList.size() - 1) {
                    position = 0;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

                InitMediaPlayer(position);
                mediaPlayer.start();
                btnPlay.setImageResource(R.drawable.music_play_pause_icon);
                imgCD.clearAnimation();
                imgCD.startAnimation(animation);

                SetTotalTime();
                UpdateCurrentTime();

                if(idFavoriteList.contains(songList.get(position).id) || newIdFavoriteList.contains(songList.get(position).id))
                {
                    isFavorite = true;
                    btnFavorite.setImageResource(R.drawable.favorited);
                }
                else {
                    isFavorite = false;
                    btnFavorite.setImageResource(R.drawable.favorite);
                }

            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --position;
                if (position < 0) {
                    position = songList.size() - 1;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                InitMediaPlayer(position);
                mediaPlayer.start();
                btnPlay.setImageResource(R.drawable.music_play_pause_icon);
                imgCD.clearAnimation();
                imgCD.startAnimation(animation);

                SetTotalTime();
                UpdateCurrentTime();

                if(idFavoriteList.contains(songList.get(position).id) || newIdFavoriteList.contains(songList.get(position).id))
                {
                    isFavorite = true;
                    btnFavorite.setImageResource(R.drawable.favorited);
                }
                else {
                    isFavorite = false;
                    btnFavorite.setImageResource(R.drawable.favorite);
                }

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();

                btnPlay.setImageResource(R.drawable.music_play_play_icon);
                imgCD.clearAnimation();

                InitMediaPlayer(position);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.music_play_play_icon);
                    imgCD.clearAnimation();
                } else {
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.music_play_pause_icon);
                    imgCD.clearAnimation();
                    imgCD.startAnimation(animation);
                }
                SetTotalTime();
                UpdateCurrentTime();
            }
        });

        btnBackPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRepeated) {
                    isRepeated = false;
                    btnRepeat.setImageResource(R.drawable.repeat);
                } else {
                    isRepeated = true;
                    btnRepeat.setImageResource(R.drawable.repeated);
                }
            }
        });

        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMuted) {
                    isMuted = false;
                    btnSpeaker.setImageResource(R.drawable.speaker);
                    mediaPlayer.setVolume(1, 1);
                } else {
                    isMuted = true;
                    btnSpeaker.setImageResource(R.drawable.mute);
                    mediaPlayer.setVolume(0, 0);
                }
            }
        });

        btnSpeedControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSpeed();
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFavorite){
                    isFavorite = false;
                    btnFavorite.setImageResource(R.drawable.favorite);

                    if(idFavoriteList.contains(songList.get(position).id)){
                        idUnFavoriteList.add(songList.get(position).id);
                    }
                    else newIdFavoriteList.remove(songList.get(position).id);
                }
                else{
                    isFavorite = true;
                    btnFavorite.setImageResource(R.drawable.favorited);

                    if(idUnFavoriteList.contains(songList.get(position).id)){
                        idUnFavoriteList.remove(songList.get(position).id);
                    }
                    else newIdFavoriteList.add(songList.get(position).id);
                }
            }
        });

        sbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(sbTime.getProgress());
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;

        //Cập nhật lại favorite
        try {
            File storagePath = this.getFilesDir();

            db = SQLiteDatabase.openDatabase(storagePath.getPath() + "/PLAYLIST.db", null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i<newIdFavoriteList.size(); i++){
                db.execSQL("insert into SONGANDPLAYLIST(id,songPlaylistName) values(?, 'Favorite songs')", new Object[]{newIdFavoriteList.get(i)});
            }

            for (int i = 0; i<idUnFavoriteList.size(); i++){
                db.execSQL("delete from SONGANDPLAYLIST where songPlaylistName like 'Favorite songs' and id like ?", new Object[]{idUnFavoriteList.get(i)});
            }

            db.close();


        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }


    }

    private void DialogSpeed() {
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_speed_control);

        Spinner spnChangeSpeed = (Spinner) dialog.findViewById(R.id.spnChangeSpeed);
        Button btnXacNhan = (Button) dialog.findViewById(R.id.btnXacNhan);
        Button btnHuy = (Button) dialog.findViewById(R.id.btnHuy);

        String[] items = new String[]{"0.25", "0.5", "0.75", "1", "1.25", "1.75", "2"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spnChangeSpeed.setAdapter(adapter);

        spnChangeSpeed.setSelection(spinnerSelected);

        spnChangeSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                speed = Float.parseFloat((String) parent.getItemAtPosition(position));
                spinnerSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                btnPlay.setImageResource(R.drawable.music_play_pause_icon);
                dialog.dismiss();
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void UpdateCurrentTime() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null)
                {
                    SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
                    txtCurrentTime.setText(formatTime.format(mediaPlayer.getCurrentPosition()));
                    sbTime.setProgress(mediaPlayer.getCurrentPosition());

                    //Nếu đã phát hết bài hát hiện tại, tự động phát bài hát tiếp theo
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (!isRepeated) {
                                ++position;
                                if (position > songList.size() - 1) {
                                    position = 0;
                                }
                            }
                            InitMediaPlayer(position);

                            mediaPlayer.start();
                            btnPlay.setImageResource(R.drawable.music_play_pause_icon);
                            imgCD.clearAnimation();
                            imgCD.startAnimation(animation);

                            SetTotalTime();
                            UpdateCurrentTime();
                        }
                    });

                    handler.postDelayed(this, 500);
                }



            }
        }, 100);

        if(isMuted) {
            mediaPlayer.setVolume(0, 0);
        }
    }

    private void SetTotalTime() {
        SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
        if(mediaPlayer!=null)
        {
            txtTotalTime.setText(formatTime.format(mediaPlayer.getDuration()));
            sbTime.setMax(mediaPlayer.getDuration());
        }

    }

    private void InitMediaPlayer(int position) {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.stop();

        SongInfo song = songList.get(position);
        mediaPlayer = new MediaPlayer();
        try{
//            mediaPlayer.setDataSource(PlayMusicActivity.this,song.uri);
            mediaPlayer.setDataSource(song.path);
            mediaPlayer.prepare();
        }catch (Exception e){}

        txtTitle.setText(song.name);

    }

    private void loadSong(ArrayList<String> idList){
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);

        songList = new ArrayList<>();

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
                    songList.add(song);
                }
            }
        }
    }

    private void AnhXa() {
        txtTitle = (TextView) findViewById(R.id.txtTitle);

        imgCD = (ImageView) findViewById(R.id.imgCD);

        txtCurrentTime = (TextView) findViewById(R.id.txtCurrentTime);
        txtTotalTime = (TextView) findViewById(R.id.txtTotalTime);
        sbTime = (SeekBar) findViewById(R.id.sbTimePM);

        btnPrev = (ImageButton) findViewById(R.id.btnPrevious);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnFavorite = (ImageButton) findViewById(R.id.btnFavorite);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnSpeaker = (ImageButton) findViewById(R.id.btnSpeaker);
        btnSpeedControl = (ImageButton) findViewById(R.id.btnSpeedControl);
        btnBackPM=(ImageButton) findViewById(R.id.btnBackPM);
    }
}