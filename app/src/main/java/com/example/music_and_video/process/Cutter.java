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

public class Cutter {
    public static void videoTrimmer(Context context,String  fileSource, String fileOutput, double startSecond, double endSecond, String extension) {


        String root = Environment.getExternalStorageDirectory().toString();
        String finalVideo = root + "/Movies/" + fileOutput + extension;

        if (fileSource.equals(finalVideo)) {
            Toast.makeText(context, "Output can not have the same path as input.\n"
                    + "Try renaming output.", Toast.LENGTH_LONG).show();
            return;
        }

        File ffmpegFile = new File(finalVideo);
        if (ffmpegFile.exists()) {
            Toast.makeText(context, "A file with the same path as output has existed.\n"
                    + "Try renaming output.", Toast.LENGTH_LONG).show();
            return;
        }

        String cmd =  "-i \""+ fileSource +"\" -q 5 -ss "+ startSecond +" -to "+ endSecond + " -y -metadata title=\""+fileOutput+"\" "+"\"" + finalVideo + "\"";

        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait\nTrimming ...\nDo not turn off the app to prevent memory leaking");
        dialog.setCancelable(false);
        dialog.show();

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {

            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    Log.i(Config.TAG, "Async command execution completed successfully.");
                    Toast.makeText(context, "Trimming succeeded", Toast.LENGTH_LONG).show();
                    File newFile = new File(finalVideo);
                    MediaScannerConnection.scanFile (context, new String[] {newFile.toString()}, null, null);

                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                    Toast.makeText(context, "Trimming canceled", Toast.LENGTH_LONG).show();
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                    Toast.makeText(context, "Trimming failed", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void musicTrimmer(Context context, String fileSource, String fileOutput, double startSecond, double endSecond, String extension) {

        String root = Environment.getExternalStorageDirectory().toString();
        String finalMusic = root + "/Music/" + fileOutput + extension;

        if (fileSource.equals(finalMusic)) {
            Toast.makeText(context, "Output can not have the same path as input.\n"
                    + "Try renaming output.", Toast.LENGTH_LONG).show();
            return;
        }

        File ffmpegFile = new File(finalMusic);
        if (ffmpegFile.exists()) {
            Toast.makeText(context, "A file with the same path as output has existed.\n"
                    + "Try renaming output.", Toast.LENGTH_LONG).show();
            return;
        }

        String cmd =  "-i \""+ fileSource +"\" -q 5 -ss "+ startSecond +" -to "+ endSecond + " -y -metadata title=\""+ fileOutput+"\" "+ "\"" + finalMusic + "\"";

        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait\nTrimming ...\nDo not turn off the app to prevent memory leaking");
        dialog.setCancelable(false);
        dialog.show();

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {

            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    Log.i(Config.TAG, "Async command execution completed successfully.");
                    Toast.makeText(context, "Trimming succeeded", Toast.LENGTH_LONG).show();

                    File newFile = new File(finalMusic);
                    MediaScannerConnection.scanFile (context, new String[] {newFile.toString()}, null, null);

                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                    Toast.makeText(context, "Trimming canceled", Toast.LENGTH_LONG).show();
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                    Toast.makeText(context, "Trimming failed", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
    }
}