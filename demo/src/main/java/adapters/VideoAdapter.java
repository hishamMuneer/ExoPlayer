package adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.demo.R;
import com.google.android.exoplayer2.demo.Utils;
import com.google.android.exoplayer2.demo.ZipHelper;

import java.io.File;
import java.util.List;

import models.VideoModel;

/**
 * Created by Hisham on 03/Sep/2017 - 17:07
 */

public class VideoAdapter extends ArrayAdapter<VideoModel> {
    private final Activity context;
    private final int resource;
    private final List<VideoModel> items;
    private ItemListener listener;

    public interface ItemListener {
        void onItemClicked(VideoModel model);
        void onDownloadClicked(VideoModel model, ImageView ivDownload);
    }

//    public interface DownloadListener {
//    }

    public void setItemListener(ItemListener listener){
        this.listener = listener;
    }


    public VideoAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<VideoModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.items = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
//        if (v == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


        Glide.with(context).load(items.get(position).getThumbnail()).into(ivThumb);

        tvTitle.setText(items.get(position).getName());

        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onDownloadClicked(items.get(position), ivDownload);

            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onItemClicked(items.get(position));
            }
        });

        return v;
    }
}
