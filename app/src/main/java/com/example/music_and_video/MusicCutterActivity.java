package com.example.music_and_video;

import androidx.annotation.Nullable;
import android.app.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music_and_video.process.Cutter;
import com.example.music_and_video.util.TimeConverter;
import com.example.music_and_video.util.RealPathUtil;

import org.florescu.android.rangeseekbar.RangeSeekBar;

public class MusicCutterActivity extends Activity {
    private static final int FILE_SELECT_CODE = 0;
    public static final Character[] INVALID_CHARS = {'"', '*', ':', '<', '>', '?', '\\', '|', '/', 0x7F};

    ImageButton btnBackMC;
    Button btnBrowseMC;
    Button btnStartMC;
    TextView txtFileChooseMC;
    TextView txtFileOutMC;
    TextView txtStartMC;
    TextView txtEndMC;
    RangeSeekBar sbTime;

    String srcPath;

    Spinner spinnerTypeMC;
    String[] type = {".mp3", ".wav", ".wma"};

    String selectedType;

    MediaPlayer mediaPlayer;
    ImageButton btnPlayCutter;

    int sbTimeMin;
    int sbTimeMax;

    long duration=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_cutter);

        srcPath = "";
        selectedType = ".mp3";

        //connect widget
        btnBackMC= (ImageButton) findViewById(R.id.btnBackMC);
        btnBrowseMC=(Button)findViewById(R.id.btnBrowseMC);
        btnStartMC=(Button)findViewById(R.id.btnStartMC);

        txtFileChooseMC =(TextView)findViewById(R.id.txtFileChooseMC);
        txtFileChooseMC.setEnabled(false);
        txtFileOutMC =(TextView)findViewById(R.id.txtFileOutMC);
        txtStartMC=(TextView)findViewById(R.id.txtStartMC);
        txtEndMC=(TextView)findViewById(R.id.txtEndMC);

        sbTime = (RangeSeekBar) findViewById(R.id.sbTimeVC);

        btnPlayCutter = (ImageButton) findViewById(R.id.btnPlayCutter);

        //set up action
        btnBackMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //back to main
                finish();
            }
        });

        btnBrowseMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlayCutter.setImageResource(R.drawable.music_play_play_icon);
                }
                showFileChooser();
            }
        });

        spinnerTypeMC = (Spinner) findViewById(R.id.spinnerTypeMC);
        ArrayAdapter<String> type_list = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type);
        type_list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTypeMC.setAdapter(type_list);

        spinnerTypeMC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sbTime.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                sbTimeMin = (int) bar.getSelectedMinValue();
                sbTimeMax = (int) bar.getSelectedMaxValue();

                txtStartMC.setText(TimeConverter.convertSecondToStr(sbTimeMin/1000));
                txtEndMC.setText(TimeConverter.convertSecondToStr(sbTimeMax/1000));
                if (mediaPlayer != null)
                    mediaPlayer.seekTo(sbTimeMin);
            }
        });

        btnPlayCutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (srcPath.equals("")) {
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlayCutter.setImageResource(R.drawable.music_play_play_icon);
                } else {
                    sbTime.setRangeValues(0, mediaPlayer.getDuration());

                    mediaPlayer.start();
                    btnPlayCutter.setImageResource(R.drawable.music_play_pause_icon);
                }

                sbTime.setRangeValues(0, mediaPlayer.getDuration());
                UpdateCurrentTime();
            }
        });

        btnStartMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(srcPath.length() == 0) {
                    Toast.makeText(MusicCutterActivity.this,"Please choose an input file",Toast.LENGTH_LONG).show();
                    return;
                }

                String desPath=String.valueOf(txtFileOutMC.getText());
                if (desPath.length() == 0) {
                    Toast.makeText(MusicCutterActivity.this,"Please enter output name",Toast.LENGTH_LONG).show();
                    return;
                }

                for (int i = 0; i < desPath.length(); i++) {
                    for (int j = 0; j < INVALID_CHARS.length; j++) {
                        if (desPath.charAt(i) == INVALID_CHARS[j]) {
                            Toast.makeText(MusicCutterActivity.this, "Invalid output name.\n"
                                    + "Try another one.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    if (desPath.charAt(i) == '.') {
                        Toast.makeText(MusicCutterActivity.this, "Output file's name could not contain extension here.\n"
                                + "Try another one.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlayCutter.setImageResource(R.drawable.music_play_play_icon);
                }

                String start=String.valueOf(txtStartMC.getText());
                String end=String.valueOf(txtEndMC.getText()) ;

                //check input time
                double startSecond = TimeConverter.convertStringToSeconds(start);
                double endSecond = TimeConverter.convertStringToSeconds(end);

                //check error convert time
                if((startSecond==-1)||(endSecond==-1))
                {
                    Toast.makeText(MusicCutterActivity.this,"Error input time. Right format is: hh:mm:ss",Toast.LENGTH_LONG).show();
                    return;
                }
                //check start time or end time>= duration

                //check start time>= end time
                if(startSecond>=endSecond)
                {
                    Toast.makeText(MusicCutterActivity.this,"Start second can't be equal or greater than end second",Toast.LENGTH_LONG).show();
                    return;
                }

                Cutter.musicTrimmer(MusicCutterActivity.this, srcPath, desPath, startSecond,endSecond, selectedType);
            }
        });
    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
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

                    //File file=new File(new URI(path));
                    String filePath= RealPathUtil.getRealPath(this, uri);
                    srcPath = filePath;

                    String displayPath=filePath.substring(filePath.lastIndexOf("/")+1);
                    txtFileChooseMC.setText(displayPath);

                    if(mediaPlayer!=null && mediaPlayer.isPlaying())
                    {
                        mediaPlayer.stop();
                    }
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }
                    mediaPlayer=new MediaPlayer();
                    try
                    {
                        mediaPlayer.setDataSource(srcPath);
                        mediaPlayer.prepare();
                        duration = mediaPlayer.getDuration();
                        txtEndMC.setText(TimeConverter.convertSecondToStr((int)duration/1000));
                        txtStartMC.setText("00:00:00");
                        sbTime.setRangeValues(0,duration);
                        sbTime.setSelectedMinValue(0);
                        sbTime.setSelectedMaxValue(duration);
                    }
                    catch (Exception e)
                    {

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
                if(mediaPlayer!=null)
                {
                    if (mediaPlayer.getCurrentPosition() > (int) sbTime.getSelectedMaxValue()) {
                        mediaPlayer.pause();
                        btnPlayCutter.setImageResource(R.drawable.music_play_play_icon);
                        mediaPlayer.seekTo(sbTimeMin);
                    }

                    handler.postDelayed(this, 500);
                }

            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer == null) {
            return;
        }
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}