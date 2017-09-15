package com.novo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novo.R;
import com.novo.adapters.BannerAdapter;
import com.novo.adapters.ContentAdapter;
import com.novo.models.VideoContent;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;


public class HomeFragment extends Fragment {
    private static ViewPager mPager;
    private ArrayList<String> ImagesArray = new ArrayList<>();
    private static final String[] STRINGS = {"http://lorempixel.com/750/400/nightlife/", "http://lorempixel.com/750/400/technics/",
            "http://lorempixel.com/750/400/nature/", "http://lorempixel.com/750/400/city/"};

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView3;
    private RecyclerView recyclerView4;
    private ArrayList<VideoContent> videoContents = new ArrayList<>();
    private String[] videoName = {"Hello", "Inception", "world", "Interstellar", "True", "Amazon", "Coming", "Test"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.fragment_home, container, false);

        for (int i = 0; i < STRINGS.length; i++)
            ImagesArray.add(STRINGS[i]);

        init();

        mPager = (ViewPager) view.findViewById(R.id.pager);
        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        recyclerView1 = (RecyclerView) view.findViewById(R.id.recycler_view1);
        recyclerView2 = (RecyclerView) view.findViewById(R.id.recycler_view2);
        recyclerView3 = (RecyclerView) view.findViewById(R.id.recycler_view3);
        recyclerView4 = (RecyclerView) view.findViewById(R.id.recycler_view4);

        mPager.addOnPageChangeListener(onPageChangeListener);
        mPager.setAdapter(new BannerAdapter(this, ImagesArray));
        indicator.setViewPager(mPager);

        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        ContentAdapter contentAdapter1 = new ContentAdapter(getActivity(), R.layout.row_content, videoContents);
        recyclerView1.setAdapter(contentAdapter1);

        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        ContentAdapter contentAdapter2 = new ContentAdapter(getActivity(), R.layout.row_content, videoContents);
        recyclerView2.setAdapter(contentAdapter2);

        recyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        ContentAdapter contentAdapter3 = new ContentAdapter(getActivity(), R.layout.row_content, videoContents);
        recyclerView3.setAdapter(contentAdapter3);

        recyclerView4.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        ContentAdapter contentAdapter4 = new ContentAdapter(getActivity(), R.layout.row_content, videoContents);
        recyclerView4.setAdapter(contentAdapter4);

        return view;
    }

    private void init() {
        for(int i = 0; i < videoName.length; i++) {
            VideoContent videoContent = new VideoContent();
            videoContents.add(videoContent.setVideoName(videoName[i]));
        }
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
