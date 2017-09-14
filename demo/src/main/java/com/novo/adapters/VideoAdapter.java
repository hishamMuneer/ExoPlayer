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
    private final LayoutInflater inflater;

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
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Nullable
    @Override
    public VideoModel getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(resource, parent, false);
            viewHolder.ivThumb = (ImageView) convertView.findViewById(R.id.ivThumb);
            viewHolder.ivDownload = (ImageView) convertView.findViewById(R.id.ivDownload);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvPercentage = (TextView) convertView.findViewById(R.id.tvPercentage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvPercentage.setText("");

        File dir = new File(new Utils(activity).getStorageDirectoryExtracts() + items.get(position).getVideoId());
        File[] file = dir.listFiles();
        if(Utils.isFolderPresent(dir) && ZipHelper.searchFile(file, null)){
            viewHolder.ivDownload.setImageResource(R.drawable.ic_delete_black_24dp);
        } else {
            viewHolder.ivDownload.setImageResource(R.drawable.ic_file_download_black_24dp);
        }


        Glide.with(activity).load(items.get(position).getThumbnail()).into(viewHolder.ivThumb);
        viewHolder.tvTitle.setText(items.get(position).getName());

        viewHolder.ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    final File dir = new File(new Utils(activity).getStorageDirectoryExtracts() + items.get(position).getVideoId());
                    final File[] file = dir.listFiles();
                    if (Utils.isFolderPresent(dir) && ZipHelper.searchFile(file, null)) { // if file is present
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setTitle(R.string.delete); // set title
                        alertDialogBuilder.setMessage(R.string.do_you_want_to_delete) // set dialog message
                                .setCancelable(false)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        listener.onDeleteClicked(dir, viewHolder.ivDownload);
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
                        alertDialog.show(); // show it
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setTitle(R.string.download); // set title
                        alertDialogBuilder.setMessage(R.string.do_you_want_to_download) // set dialog message
                                .setCancelable(false)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        listener.onDownloadClicked(items.get(position), viewHolder.ivDownload);
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onVideoPlayClicked(items.get(position));
            }
        });

        return convertView;
    }

    public class ViewHolder {
        ImageView ivThumb;
        ImageView ivDownload;
        TextView tvTitle;
        TextView tvPercentage;
    }

}
