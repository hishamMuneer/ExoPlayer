package com.novo.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.novo.R;
import com.novo.adapters.BannerAdapter;
import com.novo.adapters.ContentAdapter;
import com.novo.models.CategoryContentModel;
import com.novo.models.CategoryItemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.relex.circleindicator.CircleIndicator;


public class HomeFragment extends Fragment {
    private static ViewPager mPager;
    private ArrayList<String> ImagesArray = new ArrayList<>();
    private static final String[] STRINGS = {
            "https://drmdemo-94ea7.firebaseapp.com/280x280_2.png",
            "https://drmdemo-94ea7.firebaseapp.com/200x200_2.png",
            "https://drmdemo-94ea7.firebaseapp.com/200x200_3.png",
            "https://drmdemo-94ea7.firebaseapp.com/200x200_4.png",
            "https://drmdemo-94ea7.firebaseapp.com/200x200_5.png",
            "https://drmdemo-94ea7.firebaseapp.com/280x280_1.png"
    };

    private ArrayList<CategoryContentModel> categoryContentModels = new ArrayList<>();

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
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mPager.addOnPageChangeListener(onPageChangeListener);
        mPager.setAdapter(new BannerAdapter(this, ImagesArray));
        indicator.setViewPager(mPager);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        ContentAdapter contentAdapter1 = new ContentAdapter(getActivity(), R.layout.row_content, categoryContentModels);
        recyclerView.setAdapter(contentAdapter1);

        return view;
    }

    private void init() {
        for (int i = 0; i < 10; i++) {
            CategoryContentModel categoryContentModel = new CategoryContentModel();
            categoryContentModel.setTitle("The category " + i + " pack");
            categoryContentModel.setSubTitle("Learn from around " + (i*100) + " videos");
            List<CategoryItemModel> itemModels = new ArrayList<>();

            for(int j = 0; j < 8; j++){
                CategoryItemModel itemModel = new CategoryItemModel(STRINGS[new Random().nextInt(STRINGS.length)], "Video Title: " + j);
                itemModels.add(itemModel);
            }

            categoryContentModel.setItemModels(itemModels);
            categoryContentModels.add(categoryContentModel);
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
