package com.novo.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.novo.TokenManager;
import com.novo.R;
import com.novo.fragments.DemoFragment;
import com.novo.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity implements DemoFragment.OnDemoFragInteractionListener {

    private Button btnLogin;
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.voca_header_bg)); // change action bar color
        } else {
            Toast.makeText(this, "action is null", Toast.LENGTH_SHORT).show();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.voca_header_bg_dark)); // change status bar color
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit(); // only first time

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(TokenManager.getToken())) { // logging user out
                    LogoutHelper.cleanup(HomeActivity.this);
                    loginButtonTextUpdate();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                Bundle bundle = new Bundle();
                startActivity(intent.putExtras(bundle));
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loginButtonTextUpdate();
    }

    private void loginButtonTextUpdate() {
        if (!TextUtils.isEmpty(TokenManager.getToken())) {
            btnLogin.setText("Logout");
        } else {
            btnLogin.setText("Login");
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment mFragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    bottomNavigationView.setItemBackgroundResource(R.color.md_teal_800);
                    mFragment = new HomeFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, mFragment).commit();
                    return true;
                case R.id.navigation_library:
//                    bottomNavigationView.setItemBackgroundResource(R.color.md_deep_orange_800);
                    mFragment = DemoFragment.newInstance(null, null);
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, mFragment).commit();
                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
                case R.id.navigation_dashboard:
//                    bottomNavigationView.setItemBackgroundResource(R.color.md_pink_800);
                    return true;
//                case R.id.navigation_others:
//                    return true;
            }
            return false;
        }

    };


}
