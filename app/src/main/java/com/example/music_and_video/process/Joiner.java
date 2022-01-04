package com.example.music_and_video.process;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.util.ArrayList;

public class Joiner {
    public static void videoJoiner(Context context, ArrayList<String> inputs, String outputName, String outputExtension) {
        int n = inputs.size();

        if (n == 0) {
            Toast.makeText(context, "No input. " +
                    "Please add at least 2 inputs.", Toast.LENGTH_LONG).show();
            return;
        }

        if (n == 1) {
            Toast.makeText(context, "Can not merge with only 1 input.\n" +
                    "Please add more input.", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < n-1; i++) {
            for (int j = i+1; j < n; j++) {
                if (inputs.get(i).equals(inputs.get(j))) {
                    Toast.makeText(context, "One of the inputs has the same path as another one's.\n" +
                            "Try checking the inputs.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        //Hiện tại chưa có layout để sửa vị trí lưu nên gắn địa chỉ nội bộ + tên file output
        String root = Environment.getExternalStorageDirectory().toString();
        String finalVideo = root + "/Movies/" + outputName + outputExtension;

        for (int i = 0; i < n; i++) {
            if (inputs.get(i).equals(finalVideo)) {
                Toast.makeText(context, "One of the inputs has the same path as output's path.\n" +
                        "Try renaming output.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        File ffmpegFile = new File(finalVideo);
        if (ffmpegFile.exists()) {
            Toast.makeText(context, "A file with the same path as output has existed.\n"
                    + "Try renaming output.", Toast.LENGTH_LONG).show();
            return;
        }

        String s = "-y ";
        String complex = " -filter_complex \" ";

        for (int i = 0; i < n; i++) {
            s = s + "-i \"" + inputs.get(i) + "\" ";
            complex = complex + "[" + i + ":v][" + i + ":a]";
        }

        complex = complex + " concat=n=" + n + ":v=1:a=1 [v] [a]\"";
        complex = complex + " -map [v] -map [a] ";

        String str_cmd = s + complex + "-metadata title=\""+outputName+"\" "+"\"" + finalVideo + "\"";
        Toast.makeText(context.getApplicationContext(), str_cmd,Toast.LENGTH_SHORT).show();

        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait\nMerging ...\nDo not turn off the app to prevent memory leaking");
        dialog.setCancelable(false);
        dialog.show();

        long executionId = FFmpeg.executeAsync(str_cmd, new ExecuteCallback() {

            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    Log.i(Config.TAG, "Async command execution completed successfully.");
                    Toast.makeText(context, "Merging succeeded", Toast.LENGTH_LONG).show();

                    File newFile = new File(finalVideo);
                    MediaScannerConnection.scanFile (context, new String[] {newFile.toString()}, null, null);

                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                    Toast.makeText(context, "Merging canceled", Toast.LENGTH_LONG).show();
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                    Toast.makeText(context, "Merging failed", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void musicJoiner(Context context, ArrayList<String> inputs, String outputName, String outputExtension) {
        int n = inputs.size();

        if (n == 0) {
            Toast.makeText(context, "No input. " +
                    "Please add at least 2 inputs.", Toast.LENGTH_LONG).show();
            return;
        }

        if (n == 1) {
            Toast.makeText(context, "Can not merge with only 1 input.\n" +
                    "Please add more input.", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < n-1; i++) {
            for (int j = i+1; j < n; j++) {
                if (inputs.get(i).equals(inputs.get(j))) {
                    Toast.makeText(context, "One of the inputs has the same path as another one's.\n" +
                            "Try checking the inputs.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        //Hiện tại chưa có layout để sửa vị trí lưu nên gắn địa chỉ nội bộ + tên file output
        String root = Environment.getExternalStorageDirectory().toString();
        String finalMusic = root + "/Music/" + outputName + outputExtension;

        for (int i = 0; i < n; i++) {
            if (inputs.get(i).equals(finalMusic)) {
                Toast.makeText(context, "One of the inputs has the same path as output's path.\n" +
                        "Try renaming output.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        File ffmpegFile = new File(finalMusic);
        if (ffmpegFile.exists()) {
            Toast.makeText(context, "A file with the same path as output has existed.\n"
                    + "Try renaming output.", Toast.LENGTH_LONG).show();
            return;
        }

        String s = "-y ";
        String complex = " -filter_complex ";

        for (int i = 0; i < n; i++) {
            s = s + "-i \"" + inputs.get(i) + "\" ";
            complex = complex + "[" + i + ":0]";
        }

        complex = complex + "concat=n=" + n + ":v=0:a=1[out] -map [out] ";

        String str_cmd = s + complex + "-metadata title=\""+outputName+"\" "+ "\"" + finalMusic + "\"";
        Toast.makeText(context.getApplicationContext(), str_cmd,Toast.LENGTH_SHORT).show();

        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait\nMerging ...\nDo not turn off the app to prevent memory leaking");
        dialog.setCancelable(false);
        dialog.show();

        long executionId = FFmpeg.executeAsync(str_cmd, new ExecuteCallback() {

            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    Log.i(Config.TAG, "Async command execution completed successfully.");
                    Toast.makeText(context, "Merging succeeded", Toast.LENGTH_LONG).show();

                    File newFile = new File(finalMusic);
                    MediaScannerConnection.scanFile (context, new String[] {newFile.toString()}, null, null);

                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                    Toast.makeText(context, "Merging canceled", Toast.LENGTH_LONG).show();
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                    Toast.makeText(context, "Merging failed", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }
}