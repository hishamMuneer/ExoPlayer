package com.novo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novo.R;
import com.novo.adapters.BannerAdapter;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;


public class HomeFragment extends Fragment {
    private static ViewPager mPager;
    private ArrayList<String> ImagesArray = new ArrayList<>();
    private static final String[] STRINGS = {"http://lorempixel.com/750/400/nightlife/", "http://lorempixel.com/750/400/technics/",
            "http://lorempixel.com/750/400/nature/", "http://lorempixel.com/750/400/city/"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.fragment_home, container, false);

        for (int i = 0; i < STRINGS.length; i++)
            ImagesArray.add(STRINGS[i]);

        mPager = (ViewPager) view.findViewById(R.id.pager);
        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);

        mPager.addOnPageChangeListener(onPageChangeListener);
        mPager.setAdapter(new BannerAdapter(this, ImagesArray));
        indicator.setViewPager(mPager);

        return view;
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

            int currentPage = mPager.getCurrentItem();       //ViewPager Type

//            if (currentPage == IMAGES.length - 1 || currentPage == 0){
//                previousState = currentState;
//                currentState = state;
//                if (previousState == 1 && currentState == 0){
//                    mPager.setCurrentItem(currentPage == 0 ? IMAGES.length - 1 : 0);
//                }
//            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
