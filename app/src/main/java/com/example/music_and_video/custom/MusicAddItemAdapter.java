package com.example.music_and_video.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.music_and_video.R;
import com.example.music_and_video.custom.SongInfo;

import java.util.ArrayList;

public class MusicAddItemAdapter extends ArrayAdapter<SongInfo> {
    Context context;
    public ArrayList<SongInfo> mylistSong;
    public ArrayList<String> selectedId;
    public ArrayList<String> addId= new ArrayList<>();
    public ArrayList<String> deleteId= new ArrayList<>();

    public MusicAddItemAdapter(Context context, int layoutToBrInflated , ArrayList<SongInfo> mylistSong, ArrayList<String> idList){
        super(context,layoutToBrInflated,mylistSong);
        this.context = context;
        this.mylistSong = mylistSong;
        this.selectedId = idList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.music_add_item, null);

        SongInfo song = mylistSong.get(position);

        TextView txtname= (TextView) row.findViewById(R.id.txtName);
        TextView txtSinger = (TextView) row.findViewById(R.id.txtSinger);
        CheckBox selected = (CheckBox) row.findViewById(R.id.checkboxSelected);

        selected.setChecked(selectedId.contains(song.id));

        txtname.setText(song.name);
        txtSinger.setText(song.singer);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = selected.isChecked();
                selected.setChecked(!isSelected);

                if (isSelected)
                {
                    if(addId.contains(song.id))
                        addId.remove(song.id);
                    else deleteId.add(song.id);
                }
                else{
                    if(deleteId.contains(song.id))
                        deleteId.remove(song.id);
                    else addId.add(song.id);
                }
            }
        });

        selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = selected.isChecked();

                if (isSelected)
                {
                    if(deleteId.contains(song.id))
                        deleteId.remove(song.id);
                    else addId.add(song.id);

                }
                else{
                    if(addId.contains(song.id))
                        addId.remove(song.id);
                    else deleteId.add(song.id);
                }
            }
        });
        return (row);
    }
}
