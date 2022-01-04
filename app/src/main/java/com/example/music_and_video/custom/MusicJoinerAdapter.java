package com.example.music_and_video.custom;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.music_and_video.R;
import com.example.music_and_video.custom.MJItem;

import java.util.ArrayList;


public class MusicJoinerAdapter  extends ArrayAdapter<MJItem> {
    Context context;

    ArrayList<MJItem> MJList;

    public static class ViewHolder {
        private TextView txtNameMJ;
        private TextView txtSizeMJ;
        private TextView txtTimeMJ;
    }

    public MusicJoinerAdapter(Context context, int layoutToBeInflated, ArrayList<MJItem> MJList) {
        super(context, R.layout.row_musicjoin, MJList);
        this.context = context;
        this.MJList = MJList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row_musicjoin, null);

            viewHolder = new ViewHolder();
            viewHolder.txtNameMJ = (TextView) convertView.findViewById(R.id.txtNameMJ);
            viewHolder.txtSizeMJ = (TextView) convertView.findViewById(R.id.txtSizeMJ);
            viewHolder.txtTimeMJ = (TextView) convertView.findViewById(R.id.txtTimeMJ);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtNameMJ.setText(MJList.get(position).getName());
        viewHolder.txtSizeMJ.setText(MJList.get(position).getSize());
        viewHolder.txtTimeMJ.setText(MJList.get(position).getDuration());

        return convertView;
    }
}
