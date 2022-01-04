package com.example.music_and_video;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
//import android.media.MediaPlayer;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.music_and_video.custom.VideoInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class PlayVideoActivity extends Activity {

    boolean isVisible;
    long duration;
//    TextView txtName;
    TextView txtTitlePV;

    VideoView vvPlayVideo;
    ImageButton btnPreviousPV;
    ImageButton btnPlayPV;
    ImageButton btnStopPV;
    ImageButton btnNextPV;
    ImageButton btnBackPV;
    ImageButton btnChangeOrientation;
    ImageButton btnSpeedControlPV;
    ImageButton btnCaptureScreenPV;

    SeekBar sbTimePV;
    TextView txtTotalTimePV;
    TextView txtCurrentTimePV;
    ArrayList<String> idList = new ArrayList<>();
    ArrayList<VideoInfo> videoList;

    boolean canPlay=false;

    static float speedPV = 1;
    static  int spinnerSelectedPV = 3;

    LinearLayout btnGroupLinear;
    int position;
    boolean isRepeated = false;
    boolean isPortait=true;

//    static MediaPlayer mediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        isVisible=true;

        //connect widget


//        btnPlayVideo = (Button) findViewById(R.id.btnPlayVideo);
        vvPlayVideo = (VideoView) findViewById(R.id.vvPlayVideo);

        txtCurrentTimePV=(TextView) findViewById(R.id.txtCurrentTimePV);
        txtTotalTimePV=(TextView) findViewById(R.id.txtTotalTimePV);
        txtTitlePV=(TextView)findViewById(R.id.txtTitlePV);

        sbTimePV=(SeekBar) findViewById(R.id.sbTimePV);

        btnPreviousPV=(ImageButton)findViewById(R.id.btnPreviousPV);
        btnPlayPV=(ImageButton)findViewById(R.id.btnPlayPV);
        btnNextPV=(ImageButton)findViewById(R.id.btnNextPV);
        btnStopPV=(ImageButton)findViewById(R.id.btnStopPV);
        btnBackPV=(ImageButton)findViewById(R.id.btnBackPV);
        btnChangeOrientation=(ImageButton)findViewById((R.id.btnChangeOrientation));
        btnSpeedControlPV=(ImageButton)findViewById(R.id.btnSpeedControlPV);
        btnCaptureScreenPV =(ImageButton)findViewById(R.id.btnCaptureScreenPV);

        btnGroupLinear=(LinearLayout)findViewById(R.id.btnGroupLinear);

        Intent receiveIntent = getIntent();
        Bundle receiveBundle = receiveIntent.getExtras();

        idList = receiveBundle.getStringArrayList("idList");
        position = receiveBundle.getInt("position");

        loadVideo(idList);

        InitVideoPlayer(position);
//        vvPlayVideo.start();
//        SetTotalTime();
        btnPlayPV.setImageResource(R.drawable.music_play_pause_icon);
        UpdateCurrentTime();
        String path = videoList.get(position).path;

        if(path != null)
        {
            vvPlayVideo.setVideoPath(path);
//            vvPlayVideo.setVideoURI(Uri.parse(path));

            vvPlayVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                        if(vvPlayVideo.isPlaying())
                            vvPlayVideo.stopPlayback();

                        duration=vvPlayVideo.getDuration();
                        mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(speedPV));

                        SetTotalTime();

                }




            });


        }
        vvPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible)
                {
                    btnGroupLinear.setVisibility(View.INVISIBLE);

                }
                else
                {
                    btnGroupLinear.setVisibility(View.VISIBLE);

                }
                isVisible=!isVisible;
            }
        });

        btnSpeedControlPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSpeed();
            }
        });

        btnBackPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //set up action
        sbTimePV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                vvPlayVideo.seekTo(sbTimePV.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vvPlayVideo.seekTo(sbTimePV.getProgress());
            }
        });

        btnNextPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position++;
                if(position>videoList.size()-1)
                {
                    position=0;
                }
                if(vvPlayVideo.isPlaying())
                {
                    vvPlayVideo.pause();

                }
                InitVideoPlayer(position);
//                vvPlayVideo.start();
                btnPlayPV.setImageResource(R.drawable.music_play_pause_icon);
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
        btnPreviousPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                --position;
                if (position < 0) {
                    position = videoList.size() - 1;
                }
                if (vvPlayVideo.isPlaying()) {
                    vvPlayVideo.stopPlayback();
                }
                InitVideoPlayer(position);
//                vvPlayVideo.start();

                btnPlayPV.setImageResource(R.drawable.music_play_pause_icon);
//                imgCD.clearAnimation();
//                imgCD.startAnimation(animation);

//                SetTotalTime();


            }
        });

        btnCaptureScreenPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vvPlayVideo!=null)
                {
                    int currentPos=vvPlayVideo.getCurrentPosition();

                    MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(videoList.get(position).path);
                    Bitmap bmFrame=mediaMetadataRetriever.getFrameAtTime(currentPos*1000);
                    if(bmFrame==null)
                    {
                        Toast.makeText(PlayVideoActivity.this,"Capture failed",Toast.LENGTH_SHORT);
                    }
                    else {
                        AlertDialog captureImageDialog =
                                new AlertDialog.Builder(PlayVideoActivity.this).setTitle("Capture Image")
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(PlayVideoActivity.this,"Captured",Toast.LENGTH_SHORT).show();
                                        String root= Environment.getExternalStorageDirectory().toString();
                                        String outpath=root+"/Pictures/"+videoList.get(position).name+"_"+String.valueOf(currentPos)+".jpg";
                                        File out=new File(outpath);
                                        try {
                                            FileOutputStream outStream=new FileOutputStream(out);
                                            bmFrame.compress(Bitmap.CompressFormat.JPEG,90,outStream);
                                            outStream.flush();
                                            outStream.close();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).create();
                        ImageView capturedImageView = new ImageView(PlayVideoActivity.this);
                        capturedImageView.setImageBitmap(bmFrame);
                        ViewGroup.LayoutParams capturedImageViewLayoutParams =
                                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        capturedImageView.setLayoutParams(capturedImageViewLayoutParams);

                        captureImageDialog.setView(capturedImageView);
                        captureImageDialog.show();

                        Toast.makeText(PlayVideoActivity.this,"Captured screen is saved",Toast.LENGTH_SHORT);
                    }
                }
            }
        });
        btnChangeOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPortait)
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                else
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                isPortait=!isPortait;
            }
        });

        btnStopPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vvPlayVideo.isPlaying())
                {     vvPlayVideo.pause();
                vvPlayVideo.seekTo(0);


//                    vvPlayVideo.stopPlayback();
//                    InitVideoPlayer(position);
//                    canPlay=false;
//                    sbTimePV.setProgress(0);
//                    UpdateCurrentTime();
                    btnPlayPV.setImageResource(R.drawable.music_play_play_icon);
                }
                else
                {
//                    sbTimePV.setProgress(0);
                    vvPlayVideo.seekTo(0);
                    btnPlayPV.setImageResource(R.drawable.music_play_play_icon);
//                    InitVideoPlayer(position);
                }


//                btnPlayPV.setImageResource(R.drawable.music_play_pause_icon);
//                vvPlayVideo.seekTo(0);


//                sbTimePV.setProgress(0);
//                vvPlayVideo.seekTo(sbTimePV.getProgress());

//                SetTotalTime();
            }
        });

        btnPlayPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vvPlayVideo.isPlaying()) {
                    vvPlayVideo.pause();
                    btnPlayPV.setImageResource(R.drawable.music_play_play_icon);
//                    canPlay=false;

                } else {
//                    vvPlayVideo.start();
                    btnPlayPV.setImageResource(R.drawable.music_play_pause_icon);
                    vvPlayVideo.start();
//                    canPlay=true;

//                    imgCD.clearAnimation();
//                    imgCD.startAnimation(animation);
                }
//                SetTotalTime();
                UpdateCurrentTime();
            }
        });

    }



    private void loadVideo(ArrayList<String> idList) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        videoList = new ArrayList<>();

        if (cursor != null) {

            while(cursor.moveToNext()) {
                int index = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                String id = cursor.getString(index);

                if (idList.contains(id))
                {
                    index = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                    String name = cursor.getString(index);

                    index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    String path = cursor.getString(index);

                    index = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
                    int size = cursor.getInt(index);

                    index = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                    int length = cursor.getInt(index);

                    VideoInfo video = new VideoInfo(id, name, path, size,length);

                    videoList.add(video);
                }
            }
        }
    }
    private void InitVideoPlayer(int position) {

        if (vvPlayVideo != null && vvPlayVideo.isPlaying())
            vvPlayVideo.stopPlayback();

        VideoInfo video=videoList.get(position);
        //mediaPlayer.setDataSource(PlayMusicActivity.this,song.uri);
//        vvPlayVideo=new VideoView(PlayVideoActivity.this);
        vvPlayVideo.setVideoPath(video.path);

//        SetTotalTime();
//        UpdateCurrentTime();

        txtTitlePV.setText(video.name);
//        vvPlayVideo.start();
    }
    public void SetTotalTime() {
        SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
        txtTotalTimePV.setText(formatTime.format(vvPlayVideo.getDuration()));
        sbTimePV.setMax(vvPlayVideo.getDuration());
    }
    private void UpdateCurrentTime() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(vvPlayVideo!=null)
                {
                    SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
                    txtCurrentTimePV.setText(formatTime.format(vvPlayVideo.getCurrentPosition()));
                    sbTimePV.setProgress(vvPlayVideo.getCurrentPosition());

                    //set action when compltete
                    vvPlayVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            ++position;
                            if (position >= videoList.size()) {
                                position = 0;
                                InitVideoPlayer(position);
                            }
                        }

                    });

                    //set stop video
//                    if(canPlay)
//                    {
//                        vvPlayVideo.start();
//                    }
//                    else
//                    {
//                        vvPlayVideo.stopPlayback();
//                    }
                    handler.postDelayed(this, 500);
                }
            }
        }, 100);

//        if(isMuted) {
//            mediaPlayer.setVolume(0, 0);
//        }
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

        spnChangeSpeed.setSelection(spinnerSelectedPV);

        spnChangeSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                speedPV = Float.parseFloat((String) parent.getItemAtPosition(position));
                spinnerSelectedPV = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentTime=vvPlayVideo.getCurrentPosition();
                InitVideoPlayer(position);
                vvPlayVideo.seekTo(currentTime);
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

}