package com.example.music_and_video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.music_and_video.custom.VJItem;
import com.example.music_and_video.custom.VideoJoinerAdapter;
import com.example.music_and_video.process.Joiner;
import com.example.music_and_video.util.RealPathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class VideoJoiner extends Activity {
    private static final int FILE_SELECT_CODE = 0;
    public static final Character[] INVALID_CHARS = {'"', '*', ':', '<', '>', '?', '\\', '|', '/', 0x7F};

    ImageButton btnBackVJ;
    Button btnAddVJ, btnRemoveVJ, btnUpVJ, btnDownVj, btnMergeVJ;

    ArrayList<VJItem> arrayVJ;
    VideoJoinerAdapter adapterVJ;
    ListView listVJ;

    int crrSelectedVJ;

    Spinner spinnerTypeVJ;
    String[] type = {".mp4", ".mkv", ".wmv", ".avi"};

    String selectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_joiner);

        btnBackVJ =(ImageButton) findViewById(R.id.btnBackVJ);
        btnBackVJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        arrayVJ = new ArrayList<VJItem>();
        crrSelectedVJ = -1;

        selectedType = ".mp4";

        listVJ = (ListView) findViewById(R.id.listVJ);
        adapterVJ = new VideoJoinerAdapter(this, R.layout.row_videojoin, arrayVJ);

        listVJ.setAdapter(adapterVJ);
        listVJ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (crrSelectedVJ != -1) {
                    listVJ.getChildAt(crrSelectedVJ).setBackgroundResource(R.drawable.bg_listitem);
                }
                if (crrSelectedVJ == position) {
                    crrSelectedVJ = -1;
                    return;
                }
                listVJ.getChildAt(position).setBackgroundResource(R.drawable.bg_selected);
                crrSelectedVJ = position;
            }
        });

        btnAddVJ =(Button) findViewById(R.id.btnAddVJ);
        btnAddVJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        btnRemoveVJ =(Button) findViewById(R.id.btnRemoveVJ);
        btnRemoveVJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (crrSelectedVJ == -1) {
                    return;
                }
                if (!adapterVJ.isEmpty()) {
                    listVJ.getChildAt(crrSelectedVJ).setBackgroundResource(R.drawable.bg_listitem);

                    arrayVJ.remove(arrayVJ.get(crrSelectedVJ));

                    adapterVJ.notifyDataSetChanged();

                    crrSelectedVJ = -1;
                }
            }
        });

        btnUpVJ =(Button) findViewById(R.id.btnUpVJ);
        btnUpVJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (crrSelectedVJ == -1 || crrSelectedVJ == 0) {
                    return;
                }
                if (arrayVJ.size() >= 2) {
                    listVJ.getChildAt(crrSelectedVJ).setBackgroundResource(R.drawable.bg_listitem);

                    Collections.swap(arrayVJ, crrSelectedVJ, crrSelectedVJ-1);

                    crrSelectedVJ -= 1;

                    listVJ.getChildAt(crrSelectedVJ).setBackgroundResource(R.drawable.bg_selected);

                    adapterVJ.notifyDataSetChanged();
                }
            }
        });

        btnDownVj =(Button) findViewById(R.id.btnDownVJ);
        btnDownVj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (crrSelectedVJ == -1 || crrSelectedVJ == arrayVJ.size()-1) {
                    return;
                }
                if (arrayVJ.size() >= 2) {
                    listVJ.getChildAt(crrSelectedVJ).setBackgroundResource(R.drawable.bg_listitem);

                    Collections.swap(arrayVJ, crrSelectedVJ, crrSelectedVJ+1);

                    crrSelectedVJ += 1;

                    listVJ.getChildAt(crrSelectedVJ).setBackgroundResource(R.drawable.bg_selected);

                    adapterVJ.notifyDataSetChanged();
                }
            }
        });

        btnMergeVJ =(Button) findViewById(R.id.btnMergeVJ);
        btnMergeVJ.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (crrSelectedVJ != -1)
                    listVJ.getChildAt(crrSelectedVJ).setBackgroundResource(R.drawable.bg_listitem);

                crrSelectedVJ = -1;

                final Dialog dialog = new Dialog(VideoJoiner.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_output_joiner);

                Window window = dialog.getWindow();

                if (window == null) {
                    return;
                }

                window.setLayout(550, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.CENTER;

                window.setAttributes(windowAttributes);

                dialog.setCancelable(false);

                TextView msgNoteJoiner = dialog.findViewById(R.id.msgNoteJoiner);
                msgNoteJoiner.setText("- Enter output file and choose \"Merge\" to merge videos.");

                TextView msgNote1Joiner = dialog.findViewById(R.id.msgNote1Joiner);
                msgNote1Joiner.setText("(File name without format.)");

                TextView msgNote2Joiner = dialog.findViewById(R.id.msgNote2Joiner);
                msgNote2Joiner.setText("- Now, MusicJoiner can only create output file at the root directory address:");

                TextView msgNote3Joiner = dialog.findViewById(R.id.msgNote3Joiner);
                String root = Environment.getExternalStorageDirectory().toString() + "/Movies/";
                msgNote3Joiner.setText(root);

                EditText txtNameJoiner = dialog.findViewById(R.id.txtNameJoiner);

                Button btnCancelJoiner = dialog.findViewById(R.id.btnCancelJoiner);
                btnCancelJoiner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                Button btnOkJoiner = dialog.findViewById(R.id.btnOkJoiner);
                btnOkJoiner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String outputName = txtNameJoiner.getText().toString();
                        if (outputName.length() == 0) {
                            txtNameJoiner.setHint("");
                            Toast.makeText(VideoJoiner.this, "Please enter output name", Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (int i = 0; i < outputName.length(); i++) {
                            for (int j = 0; j < INVALID_CHARS.length; j++) {
                                if (outputName.charAt(i) == INVALID_CHARS[j]) {
                                    txtNameJoiner.setHint("Please enter another name.");
                                    Toast.makeText(VideoJoiner.this, "Invalid output name.\n"
                                            + "Try another one.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            if (outputName.charAt(i) == '.') {
                                txtNameJoiner.setHint("Please enter another name.");
                                Toast.makeText(VideoJoiner.this, "Output file's name could not contain extension here.\n"
                                        + "Try another one.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        String testPath = root + outputName + selectedType;
                        File testFile = new File(testPath);
                        if (testFile.exists()) {
                            txtNameJoiner.setHint("Please enter another name.");
                            Toast.makeText(VideoJoiner.this, "Output file has existed", Toast.LENGTH_LONG).show();
                            return;
                        }
                        dialog.dismiss();

                        int n = arrayVJ.size();

                        ArrayList<String> inputFiles = new ArrayList<>();

                        for (int i = 0; i < n; i++) {
                            inputFiles.add(arrayVJ.get(i).getPath());
                        }

                        String inputPath = "";

                        for (int i = 0; i < n; i++) {
                            inputPath += "\n" + inputFiles.get(i);
                        }

                        Toast.makeText(VideoJoiner.this, "Input path list:\n" + inputPath, Toast.LENGTH_LONG).show();

                        Joiner.videoJoiner(VideoJoiner.this, inputFiles, outputName, selectedType);
                    }
                });
                dialog.show();
            }
        });


        spinnerTypeVJ = (Spinner) findViewById(R.id.spinnerTypeVJ);
        ArrayAdapter<String> type_list = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, type);
        type_list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTypeVJ.setAdapter(type_list);

        spinnerTypeVJ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
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

                    String filePath = RealPathUtil.getRealPath(this,uri);

                    Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);

                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    
                    returnCursor.moveToFirst();

                    String name = returnCursor.getString(nameIndex);

                    float sizeInMB = (float) returnCursor.getLong(sizeIndex) / (1000*1000);
                    String size = String.format("%.2f", sizeInMB)  + " MB";

                    MediaPlayer mp = MediaPlayer.create(this, uri);
                    int duration = mp.getDuration();

                    mp.release();

                    String time =  convertMillieToHMmSs(duration);

                    VJItem video = new VJItem(name, size, time, filePath);

                    arrayVJ.add(video);
                    adapterVJ.notifyDataSetChanged();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
        else {
            return String.format("00:" + "%02d:%02d" , minute, second);
        }
    }
}
