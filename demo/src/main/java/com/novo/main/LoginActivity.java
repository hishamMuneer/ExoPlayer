package com.novo.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.novo.TokenManager;

import com.novo.R;
import com.novo.network.EndPoints;
import com.novo.network.ServerHit;
import com.novo.util.Utils;

public class LoginActivity extends Activity {

    private EditText etUserName;
    private EditText etPass;
    private Button btnLogin;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initStuff();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!Utils.isNetworkAvailable(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this, "No internet available", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = EndPoints.getBaseUrl() + "VocabimateLoginServer/webapi/myresource/login";
                String body = "username=" + etUserName.getText().toString()+"&password="+etPass.getText().toString();
                ServerHit.JSONTask task = new ServerHit.JSONTask(LoginActivity.this, TokenManager.getToken(), "POST", "application/x-www-form-urlencoded", body, new ServerHit.ServiceHitResponseListener() {
                    @Override
                    public void onDone(String response) {
                        TokenManager.setToken(response);
                        finish();
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
                task.execute(url);
            }
        });
    }

    private void initStuff() {
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPass = (EditText) findViewById(R.id.etPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
    }
}
