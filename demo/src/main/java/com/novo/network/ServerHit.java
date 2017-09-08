package com.novo.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer2.upstream.novo.TokenManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.novo.util.Utils.TAG;

/**
 * Created by Hisham on 25/Aug/2017 - 20:01
 */

public class ServerHit {

    public interface ServiceHitResponseListener{
        void onDone(String response);
        void onError(String error);
    }


    public static class JSONTask extends AsyncTask<String,String, String > {

        private final ServiceHitResponseListener listener;
        private final String type;
        private String body;
        private String contentType;
        private final Activity activity;
        private ProgressDialog progressDialog;
        private String token;

        public JSONTask(Activity activity, String token, String type, String contentType, String body, ServiceHitResponseListener listener){
            this.body = body;
            this.listener = listener;
            this.type = type;
            this.contentType = contentType;
            this.activity = activity;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // hit server to get all videos urls
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                if(!TextUtils.isEmpty(type)) {
                    connection.setRequestMethod(type);
                }
                if(!TextUtils.isEmpty(contentType)) {
                    connection.setRequestProperty("Content-Type", contentType);
                }
                if(!TextUtils.isEmpty(token)) {
                    connection.setRequestProperty("token", TokenManager.getToken());
                }
                if(body != null) {
                    byte[] outputInBytes = body.getBytes("UTF-8");
                    OutputStream os = connection.getOutputStream();
                    os.write(outputInBytes);
                    os.close();
                }

                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
                listener.onError(e.getMessage());
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if(result != null) {
                Log.d(TAG, "onPostExecute: " + result);
                listener.onDone(result);
            } else {
                listener.onError(null);
//                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}
