package com.google.android.exoplayer2.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.novo.TokenManager;

public class MainActivity extends Activity {

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initStuff();
    }

    private void initStuff() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnOpen = (Button) findViewById(R.id.btnOpen);
        Button btnRestricted = (Button) findViewById(R.id.btnRestricted);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(TokenManager.getToken())) {
                    TokenManager.setToken(null);
                    loginButtonTextUpdate();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                Bundle bundle = new Bundle();
                startActivity(intent.putExtras(bundle));
            }
        });

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.setData(Uri.parse("http://35.154.11.202/VocabimateContentServer/m3u8-encrypted-open/master.m3u8"));
                intent.setAction(PlayerActivity.ACTION_VIEW);
                startActivity(intent);
            }
        });

        btnRestricted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.setData(Uri.parse("http://35.154.11.202/VocabimateContentServer/m3u8-encrypted-auth/master.m3u8"));
                intent.setAction(PlayerActivity.ACTION_VIEW);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginButtonTextUpdate();
    }

    private void loginButtonTextUpdate() {
        if(!TextUtils.isEmpty(TokenManager.getToken())){
            btnLogin.setText("Logout");
        } else {
            btnLogin.setText("Login");
        }
    }
}
