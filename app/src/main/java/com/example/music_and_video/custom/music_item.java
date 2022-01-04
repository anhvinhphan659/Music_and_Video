package com.example.music_and_video.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.music_and_video.R;
import com.example.music_and_video.custom.SongInfo;

import java.util.ArrayList;

public class music_item extends ArrayAdapter<SongInfo> {
        Context context;
        ArrayList<SongInfo> mylistSong;


public music_item(Context context, int layoutToBrInflated , ArrayList<SongInfo> mylistSong){
        super(context,layoutToBrInflated,mylistSong);
        this.context = context;
        this.mylistSong = mylistSong;
        }


@Override
public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View row = inflater.inflate(R.layout.music_item, null);

        TextView txtname= (TextView) row.findViewById(R.id.txtName);
        TextView txtSinger = (TextView) row.findViewById(R.id.txtSinger);

        txtname.setText(mylistSong.get(position).name);
        txtSinger.setText(mylistSong.get(position).singer);
        return (row);
        }
}
