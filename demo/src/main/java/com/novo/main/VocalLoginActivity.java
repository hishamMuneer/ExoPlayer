package com.novo.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.LinearLayout;
import com.novo.R;

public class VocalLoginActivity extends Activity implements View.OnClickListener{

    private static final String TAG = VocalLoginActivity.class.getSimpleName();
    private LinearLayout llLoginSignUp;
    private NestedScrollView viewBottomSheet;
    private LinearLayout llLogin;
    private LinearLayout llJoinUs;
    private BottomSheetBehavior<NestedScrollView> bottomSheetBehavior;
    private LinearLayout llLoginView;
    private LinearLayout llSignUpView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocal_login);
        llLogin = (LinearLayout) findViewById(R.id.llLogin);
        llJoinUs = (LinearLayout) findViewById(R.id.llJoinUs);
        llLoginView = (LinearLayout) findViewById(R.id.llLoginView);
        llSignUpView = (LinearLayout) findViewById(R.id.llSignUpView);
        viewBottomSheet = (NestedScrollView) findViewById(R.id.viewBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);

        llLogin.setOnClickListener(this);
        llJoinUs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llLogin:
                llLogin.setBackgroundColor(getResources().getColor(R.color.primary));
                llJoinUs.setBackgroundColor(getResources().getColor(R.color.voca_header_bg));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                llLoginView.setVisibility(View.VISIBLE);
                llSignUpView.setVisibility(View.GONE);
                break;
            case R.id.llJoinUs:
                llJoinUs.setBackgroundColor(getResources().getColor(R.color.primary));
                llLogin.setBackgroundColor(getResources().getColor(R.color.voca_header_bg));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                llLoginView.setVisibility(View.GONE);
                llSignUpView.setVisibility(View.VISIBLE);
                break;
        }
    }
}
