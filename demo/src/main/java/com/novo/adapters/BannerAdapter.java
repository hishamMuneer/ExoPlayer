package com.novo.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.novo.R;

import java.util.ArrayList;

/**
 * Created by ayushgarg on 21/11/16.
 */

public class BannerAdapter extends PagerAdapter {

    private ArrayList<String> IMAGES;
    private LayoutInflater inflater;
    private Context context;


    public BannerAdapter(Fragment fragment, ArrayList<String> IMAGES) {
        this.context = fragment.getActivity();
        this.IMAGES=IMAGES;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.view_pager_fragment, view, false);

        assert imageLayout != null;
        ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.vp_image);

        Glide.with(context).load(IMAGES.get(position)).into(imageView);

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
