package com.google.android.exoplayer2.demo;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        private String body;

        public JSONTask(String body, ServiceHitResponseListener listener){
            this.body = body;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

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
                String line ="";
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
        }
    }

    private static final String TAG = ServerHit.class.getSimpleName();
}
