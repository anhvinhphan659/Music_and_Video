package com.example.music_and_video.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.music_and_video.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class video_item extends ArrayAdapter<VideoInfo> {
    Context context;
    ArrayList<VideoInfo> mylistVideo;


    public video_item(Context context, int layoutToBrInflated , ArrayList<VideoInfo> mylistVideo){
        super(context,layoutToBrInflated,mylistVideo);
        this.context = context;
        this.mylistVideo = mylistVideo;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View row = inflater.inflate(R.layout.video_item, null);

        ImageView imgVideo = (ImageView) row.findViewById(R.id.imgVideo);
        TextView txtname= (TextView) row.findViewById(R.id.txtName);
        TextView txtSize = (TextView) row.findViewById(R.id.txtSize);
        TextView txtLength = (TextView) row.findViewById(R.id.txtLength);

        Glide.with(context).load(new File(mylistVideo.get(position).path)).into(imgVideo);
        txtname.setText(mylistVideo.get(position).name);

        String convertSize;
        if (mylistVideo.get(position).size < 1024){
            convertSize = String.format(this.context.getString(R.string.size_in_b), (double) mylistVideo.get(position).size);
        }
        else if (mylistVideo.get(position).size < Math.pow(1024,2)){
            convertSize = String.format(this.context.getString(R.string.size_in_kb), (double) mylistVideo.get(position).size / 1024);

        }
        else if (mylistVideo.get(position).size < Math.pow(1024,3)){
            convertSize = String.format(this.context.getString(R.string.size_in_mb), (double) mylistVideo.get(position).size / Math.pow(1024, 2));
        }
        else {
            convertSize = String.format(this.context.getString(R.string.size_in_gb), (double) mylistVideo.get(position).size / Math.pow(1024, 3));
        }

        String convertTime;
        int sec = (mylistVideo.get(position).length / 1000) % 60;
        int min = (mylistVideo.get(position).length / (60 * 1000)) % 60;
        int hrs = mylistVideo.get(position).length / (60 * 60 * 1000);

        if(hrs == 0){
            convertTime = String.valueOf(min).concat(":".concat((String.format(Locale.UK,"%02d",sec))));
        }
        else {
            convertTime = String.valueOf(hrs)
                    .concat(":".concat(String.format(Locale.UK,"%02d",min)
                            .concat(":".concat(String.format(Locale.UK, "%02d",sec)))));
        }

        txtSize.setText(convertSize);
        txtLength.setText(convertTime);

        return (row);
    }
}
