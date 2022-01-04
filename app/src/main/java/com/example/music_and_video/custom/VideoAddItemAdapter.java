package com.example.music_and_video.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.music_and_video.R;
import com.example.music_and_video.custom.VideoInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class VideoAddItemAdapter extends ArrayAdapter<VideoInfo> {
    Context context;
    public ArrayList<VideoInfo> mylistVideo;
    public ArrayList<String> selectedId;
    public ArrayList<String> addId= new ArrayList<>();
    public ArrayList<String> deleteId= new ArrayList<>();

    public VideoAddItemAdapter(Context context, int layoutToBrInflated , ArrayList<VideoInfo> mylistVideo, ArrayList<String> idList){
        super(context,layoutToBrInflated,mylistVideo);
        this.context = context;
        this.mylistVideo = mylistVideo;
        this.selectedId = idList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.video_add_item, null);

        VideoInfo video = mylistVideo.get(position);

        ImageView imgVideo = (ImageView) row.findViewById(R.id.imgVideo);
        TextView txtname= (TextView) row.findViewById(R.id.txtName);
        TextView txtSize = (TextView) row.findViewById(R.id.txtSize);
        TextView txtLength = (TextView) row.findViewById(R.id.txtLength);
        CheckBox selected = (CheckBox) row.findViewById(R.id.checkboxSelected);

        String convertSize;
        if (video.size < 1024){
            convertSize = String.format(this.context.getString(R.string.size_in_b), (double) video.size);
        }
        else if (video.size < Math.pow(1024,2)){
            convertSize = String.format(this.context.getString(R.string.size_in_kb), (double) video.size / 1024);

        }
        else if (video.size < Math.pow(1024,3)){
            convertSize = String.format(this.context.getString(R.string.size_in_mb), (double) video.size / Math.pow(1024, 2));
        }
        else {
            convertSize = String.format(this.context.getString(R.string.size_in_gb), (double) video.size / Math.pow(1024, 3));
        }

        String convertTime;
        int sec = (video.length / 1000) % 60;
        int min = (video.length / (60 * 1000)) % 60;
        int hrs = video.length / (60 * 60 * 1000);

        if(hrs == 0){
            convertTime = String.valueOf(min).concat(":".concat((String.format(Locale.UK,"%02d",sec))));
        }
        else {
            convertTime = String.valueOf(hrs)
                    .concat(":".concat(String.format(Locale.UK,"%02d",min)
                            .concat(":".concat(String.format(Locale.UK, "%02d",sec)))));
        }

        Glide.with(context).load(new File(mylistVideo.get(position).path)).into(imgVideo);
        selected.setChecked(selectedId.contains(video.id));
        txtname.setText(video.name);
        txtSize.setText(convertSize);
        txtLength.setText(convertTime);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = selected.isChecked();
                selected.setChecked(!isSelected);

                if (isSelected)
                {
                    if(addId.contains(video.id))
                        addId.remove(video.id);
                    else deleteId.add(video.id);
                }
                else{
                    if(deleteId.contains(video.id))
                        deleteId.remove(video.id);
                    else addId.add(video.id);
                }
            }
        });

        selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = selected.isChecked();

                if (isSelected)
                {
                    if(deleteId.contains(video.id))
                        deleteId.remove(video.id);
                    else addId.add(video.id);

                }
                else{
                    if(addId.contains(video.id))
                        addId.remove(video.id);
                    else deleteId.add(video.id);
                }
            }
        });

        return (row);
    }

}
