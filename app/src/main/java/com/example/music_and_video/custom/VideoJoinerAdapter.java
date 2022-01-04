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
import com.example.music_and_video.custom.VJItem;

import java.io.File;
import java.util.ArrayList;


public class VideoJoinerAdapter  extends ArrayAdapter<VJItem> {
    Context context;

    ArrayList<VJItem> VJList;

    public static class ViewHolder {
        private ImageView imgVJ;
        private TextView txtNameVJ;
        private TextView txtSizeVJ;
        private TextView txtTimeVJ;
    }

    public VideoJoinerAdapter(Context context, int layoutToBeInflated, ArrayList<VJItem> VJList) {
        super(context, R.layout.row_videojoin, VJList);
        this.context = context;
        this.VJList = VJList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row_videojoin, null);

            viewHolder = new ViewHolder();
            viewHolder.imgVJ = (ImageView) convertView.findViewById((R.id.imgVJ));
            Glide.with(context).load(new File(VJList.get(position).getPath())).into(viewHolder.imgVJ);
            viewHolder.txtNameVJ = (TextView) convertView.findViewById(R.id.txtNameVJ);
            viewHolder.txtSizeVJ = (TextView) convertView.findViewById(R.id.txtSizeVJ);
            viewHolder.txtTimeVJ = (TextView) convertView.findViewById(R.id.txtTimeVJ);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.txtNameVJ.setText(VJList.get(position).getName());
        viewHolder.txtSizeVJ.setText(VJList.get(position).getSize());
        viewHolder.txtTimeVJ.setText(VJList.get(position).getDuration());

        return convertView;
    }
}
