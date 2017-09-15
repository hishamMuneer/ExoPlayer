package com.novo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.novo.R;
import com.novo.models.VideoContent;

import java.util.ArrayList;


/**
 * Created by ayushgarg on 02/09/17.
 */

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    Context context;
    int layoutResource;
    ArrayList<VideoContent> videoContents;

    public ContentAdapter(Context context, int layoutResource, ArrayList<VideoContent> videoContents) {
        this.context = context;
        this.layoutResource = layoutResource;
        this.videoContents = videoContents;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_video_name.setText(videoContents.get(position).getVideoName());
    }

    @Override
    public int getItemCount() {
        return videoContents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_video_name;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_video_name = (TextView) itemView.findViewById(R.id.tv_video_name);
            imageView = (ImageView) itemView.findViewById(R.id.iv_thumbnail);

        }
    }

}
