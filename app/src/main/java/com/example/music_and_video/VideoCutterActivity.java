package com.example.music_and_video;

import android.app.Activity;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.music_and_video.process.Cutter;
import com.example.music_and_video.util.RealPathUtil;
import com.example.music_and_video.util.TimeConverter;

import org.florescu.android.rangeseekbar.RangeSeekBar;

public class VideoCutterActivity extends Activity {
    private static final int FILE_SELECT_CODE = 0;
    public static final Character[] INVALID_CHARS = {'"', '*', ':', '<', '>', '?', '\\', '|', '/', 0x7F};
    ImageButton btnBackVC;
    Button btnBrowseVC;
    Button btnStartVC;
    TextView txtFileChooseVC;
    TextView txtFileOutVC;
    TextView txtStartVC;
    TextView txtEndVC;

    VideoView vvVC;
    RangeSeekBar sbTimeVC;
    ImageButton btnPlayVC;
    String srcPath;
    Spinner spinnerTypeVC;
    String[] type = {".mp4", ".mkv", ".wmv", ".avi"};

    String selectedType;

    long duration=0;

    int sbTimeMin;
    int sbTimeMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_cutter);

        srcPath = "";
        selectedType = ".mp4";
        //connect widget
        btnBackVC = (ImageButton) findViewById(R.id.btnBackVC);
        btnBrowseVC =(Button)findViewById(R.id.btnBrowseVC);
        btnStartVC =(Button)findViewById(R.id.btnStartVC);

        txtFileChooseVC =(TextView)findViewById(R.id.txtFileChooseVC);
        txtFileChooseVC.setEnabled(false);
        txtFileOutVC =(TextView)findViewById(R.id.txtFileOutVC);
        txtStartVC =(TextView)findViewById(R.id.txtStartVC);
        txtEndVC =(TextView)findViewById(R.id.txtEndVC);

        vvVC=(VideoView) findViewById(R.id.vvVC);
        sbTimeVC=(RangeSeekBar) findViewById(R.id.sbTimeVC);
        btnPlayVC = (ImageButton) findViewById(R.id.btnPlayVC);


        sbTimeVC.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if (vvVC != null) {
                    vvVC.pause();
                }
                return true;
            }
        });
        //set up action
        sbTimeVC.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                sbTimeMin = (int) bar.getSelectedMinValue();
                sbTimeMax = (int) bar.getSelectedMaxValue();
                txtStartVC.setText(TimeConverter.convertSecondToStr(sbTimeMin/1000));
                txtEndVC.setText(TimeConverter.convertSecondToStr(sbTimeMax/1000));

                if(vvVC!=null)
                    vvVC.seekTo(sbTimeMin);
            }
        });

        btnPlayVC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (srcPath.equals("")) {
                    return;
                }
                if(vvVC.isPlaying()) {
                    vvVC.pause();
                    btnPlayVC.setImageResource(R.drawable.music_play_play_icon);
                } else {
                    sbTimeVC.setRangeValues(0, vvVC.getDuration());

                    vvVC.start();
                    btnPlayVC.setImageResource(R.drawable.music_play_pause_icon);
                }
                sbTimeVC.setRangeValues(0, vvVC.getDuration());
                UpdateCurrentTime();
            }
        });

        btnBackVC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //back to main
                finish();
            }
        });
        btnBrowseVC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vvVC!=null && vvVC.isPlaying()) {
                    vvVC.pause();
                    btnPlayVC.setImageResource(R.drawable.music_play_play_icon);
                }
                showFileChooser();

            }
        });

        spinnerTypeVC = (Spinner) findViewById(R.id.spinnerTypeVC);
        ArrayAdapter<String> type_list = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, type);
        type_list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTypeVC.setAdapter(type_list);

        spinnerTypeVC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnStartVC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(srcPath.length() == 0) {
                    Toast.makeText(VideoCutterActivity.this,"Please choose an input file",Toast.LENGTH_LONG).show();
                    return;
                }

                String desPath=String.valueOf(txtFileOutVC.getText());
                if (desPath.length() == 0) {
                    Toast.makeText(VideoCutterActivity.this,"Please enter output name",Toast.LENGTH_LONG).show();
                    return;
                }

                for (int i = 0; i < desPath.length(); i++) {
                    for (int j = 0; j < INVALID_CHARS.length; j++) {
                        if (desPath.charAt(i) == INVALID_CHARS[j]) {
                            Toast.makeText(VideoCutterActivity.this, "Invalid output name.\n"
                                    + "Try another one.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    if (desPath.charAt(i) == '.') {
                        Toast.makeText(VideoCutterActivity.this, "Output file's name could not contain extension here.\n"
                                + "Try another one.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (vvVC != null && vvVC.isPlaying()) {
                    vvVC.pause();
                    btnPlayVC.setImageResource(R.drawable.music_play_play_icon);
                }

                String start=String.valueOf(txtStartVC.getText());
                String end=String.valueOf(txtEndVC.getText()) ;


                //check input time
                double startSecond = TimeConverter.convertStringToSeconds(start);
                double endSecond = TimeConverter.convertStringToSeconds(end);

                //check error convert time
                if((startSecond==-1)||(endSecond==-1))
                {
                    Toast.makeText(VideoCutterActivity.this,"Error input time. Right format is: hh:mm:ss",Toast.LENGTH_LONG).show();
                    return;
                }
                //check start time or end time>= duration

                //check start time>= end time
                if(startSecond>=endSecond)
                {
                    Toast.makeText(VideoCutterActivity.this,"Start second can't be equal or greater than end second",Toast.LENGTH_LONG).show();
                    return;
                }

                Cutter.videoTrimmer(VideoCutterActivity.this,srcPath,desPath,startSecond,endSecond, selectedType);
            }
        });
    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    Uri uri = data.getData();

                    //                    File file=new File(new URI(path));
                    String filePath= RealPathUtil.getRealPath(this,uri);
                    srcPath= filePath;


//            vvPlayVideo.setVideoURI(Uri.parse(path));


//                    Toast.makeText(VideoCutterActivity.this,String.valueOf(duration),Toast.LENGTH_SHORT).show();


                    String displayPath=filePath.substring(filePath.lastIndexOf("/")+1);
                    txtFileChooseVC.setText(displayPath);

                    if(vvVC != null && vvVC.isPlaying()){
                        vvVC.stopPlayback();
                    }
                    try {
                        vvVC.setVideoPath(srcPath);
                        vvVC.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                duration=vvVC.getDuration();
                                txtEndVC.setText(TimeConverter.convertSecondToStr((int)duration/1000));
                                txtStartVC.setText("00:00:00");
                                sbTimeVC.setRangeValues(0,duration);
                                sbTimeVC.setSelectedMinValue(0);
                                sbTimeVC.setSelectedMaxValue(duration);
//                        Toast.makeText(PlayVideoActivity.this,String.valueOf(duration),Toast.LENGTH_SHORT).show();

//                    vvPlayVideo.start();
//                    MediaController mediaController = new MediaController(PlayVideoActivity.this);
//                    mediaController.setMediaPlayer(vvPlayVideo);
//                    vvPlayVideo.setMediaController(mediaController);
                            }
                        });
                    }
                    catch (Exception e) {

                    }
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UpdateCurrentTime() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(vvVC!=null)
                {
                    if(vvVC.getCurrentPosition() > (int) sbTimeVC.getSelectedMaxValue()) {
                        vvVC.pause();
                        btnPlayVC.setImageResource(R.drawable.music_play_play_icon);
                        vvVC.seekTo(sbTimeMin);
                    }
                    handler.postDelayed(this, 500);
                }
            }
        }, 100);

//        if(isMuted) {
//            mediaPlayer.setVolume(0, 0);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vvVC == null) {
            return;
        }
        if(vvVC.isPlaying())
            vvVC.stopPlayback();
        vvVC = null;
    }
}