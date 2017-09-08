package com.novo.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.novo.R;
import com.novo.util.Utils;
import com.novo.network.ZipHelper;

import java.io.File;
import java.util.List;

import com.novo.models.VideoModel;

/**
 * Created by Hisham on 03/Sep/2017 - 17:07
 */

public class VideoAdapter extends ArrayAdapter<VideoModel> {
    private final Activity activity;
    private final int resource;
    private final List<VideoModel> items;
    private ItemListener listener;

    public interface ItemListener {
        void onVideoPlayClicked(VideoModel model);
        void onDownloadClicked(VideoModel model, ImageView ivDownload);
        void onDeleteClicked(File directory, ImageView ivDownload);

    }

//    public interface DownloadListener {
//    }

    public void setItemListener(ItemListener listener){
        this.listener = listener;
    }


    public VideoAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List<VideoModel> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.resource = resource;
        this.items = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
//        if (v == null) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(resource, parent, false);
//        }

        ImageView ivThumb = (ImageView) v.findViewById(R.id.ivThumb);
        final ImageView ivDownload = (ImageView) v.findViewById(R.id.ivDownload);
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);

         File dir = new File(Utils.getStorageDirectoryExtracts() + items.get(position).getVideoId());
         File[] file = dir.listFiles();
        if(Utils.isFolderPresent(dir) && ZipHelper.searchFile(file, null)){
            ivDownload.setImageResource(R.mipmap.ic_download_complete);
        } else {
            ivDownload.setImageResource(R.mipmap.ic_download);
        }


        Glide.with(activity).load(items.get(position).getThumbnail()).into(ivThumb);
        tvTitle.setText(items.get(position).getName());

        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    final File dir = new File(Utils.getStorageDirectoryExtracts() + items.get(position).getVideoId());
                    final File[] file = dir.listFiles();
                    if (Utils.isFolderPresent(dir) && ZipHelper.searchFile(file, null)) { // if file is present
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setTitle("Delete"); // set title
                        alertDialogBuilder.setMessage("Do you want to delete this offline video?") // set dialog message
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        listener.onDeleteClicked(dir, ivDownload);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
                        alertDialog.show(); // show it
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setTitle("Download"); // set title
                        alertDialogBuilder.setMessage("Do you want to download this video?") // set dialog message
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        listener.onDownloadClicked(items.get(position), ivDownload);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
                        alertDialog.show(); // show it

                    }
                }
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onVideoPlayClicked(items.get(position));
            }
        });

        return v;
    }
}
