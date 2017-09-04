package com.google.android.exoplayer2.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.novo.TokenManager;
import com.google.gson.Gson;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapters.VideoAdapter;
import models.VideoModel;

public class MainActivity extends Activity {

    private Button btnLogin;
//    private ImageView iVDownload;
//    private String videoId = "arc_single";
    private static final String TAG = MainActivity.class.getSimpleName();
    private GridView lvAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initStuff();
    }

    private void initStuff() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnLocal = (Button) findViewById(R.id.btnLocal);
//        iVDownload = (ImageView) findViewById(R.id.iVDownload);
        lvAll = (GridView) findViewById(R.id.lvAll);

        // hit server to get all videos urls
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        ServerHit.JSONTask task = new ServerHit.JSONTask("GET", null, null, new ServerHit.ServiceHitResponseListener() {
            @Override
            public void onDone(final String response) {
                Log.d(TAG, "onDone: " + response);

                try {
                    JSONArray array = new JSONArray(response);
                    List<VideoModel> items = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        VideoModel model = new Gson().fromJson(jsonObject.toString(), VideoModel.class);
                        items.add(model);
                    }
                    // hit server, get items and pass into adapter
                    VideoAdapter adapter = new VideoAdapter(MainActivity.this, R.layout.row_videos_grid, items);
                    adapter.setItemListener(new VideoAdapter.ItemListener() {
                        @Override
                        public void onItemClicked(final VideoModel model) {
                            File dir = new File(Utils.getStorageDirectoryExtracts() + model.getVideoId());
                            File[] file = dir.listFiles();
                            if(Utils.isFolderPresent(dir)){
                                // trying to find my file
                                Log.d(TAG, "onItemClicked: " + ZipHelper.searchFile(file, null));
                                ZipHelper.searchFile(file, new ZipHelper.FileListener() {
                                    @Override
                                    public void onFileSearchComplete(boolean fileFound, String fileToPlay) {
                                        if(fileFound && !TextUtils.isEmpty(fileToPlay)) {
                                            Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                                            intent.setData(Uri.parse(fileToPlay));
                                            intent.setAction(PlayerActivity.ACTION_VIEW);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Unable to play local video, playing stream.", Toast.LENGTH_SHORT).show();
                                            playMediaFromServer(model);
                                        }
                                    }
                                });
                            } else { // if folder is not present locally, play via server
                                playMediaFromServer(model);
                            }
                        }

                        @Override
                        public void onDownloadClicked(VideoModel model, final ImageView ivDownload) {
                            String serverFileUrl = Utils.getBaseUrl() + "VocabimateContentServer/webapi/video/download?videoId=" + model.getVideoId();
                            String videoId;
                            try {
                                Map<String, String> params = Utils.splitQuery(new URL(serverFileUrl));
                                if(params != null && params.size() > 0 && params.containsKey("videoId")) {
                                    videoId = params.get("videoId");
                                } else {
                                    Toast.makeText(MainActivity.this, "Video id not found", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (UnsupportedEncodingException | MalformedURLException e) {
                                e.printStackTrace();
                                return;
                            }
                            // execute this when the downloader must be fired
                            final File sourceZipFile = new File(Utils.getStorageDirectoryZips() + videoId);
                            String fileNameWithOutExt = FilenameUtils.removeExtension(sourceZipFile.getName());
                            final File targetDirectory = new File(Utils.getStorageDirectoryExtracts() + fileNameWithOutExt);
                            targetDirectory.mkdir();

                            final DownloadTask downloadTask = new DownloadTask(MainActivity.this, sourceZipFile.getAbsolutePath(), new DownloadTask.DownloadTaskListener() {
                                @Override
                                public void onFileDownload() {
                                    new ZipHelper.ZipTask(MainActivity.this, new ZipHelper.ZipTaskListener() {
                                        @Override
                                        public void onUnzipped(String fileToPlay) {
                                            Log.d(TAG, "onUnzipped: " + fileToPlay);
                                            ivDownload.setImageResource(R.mipmap.ic_download_complete);
                                        }
                                    }).execute(sourceZipFile, targetDirectory);
                                }
                            });
                            downloadTask.execute(serverFileUrl);

                        }
                    });
                    lvAll.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
            }
        });

        String url = Utils.getBaseUrl() +"VocabimateContentServer/webapi/video/fetchAll";
        task.execute(url);

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




        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void playMediaFromServer(VideoModel model) {
        ServerHit.JSONTask streamTask = new ServerHit.JSONTask("GET", null, null, new ServerHit.ServiceHitResponseListener() {
            @Override
            public void onDone(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String videoUrl = object.getString("videoUrl");
                    // send to player
                    Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                    intent.setData(Uri.parse(videoUrl));
                    intent.setAction(PlayerActivity.ACTION_VIEW);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
        streamTask.execute(Utils.getBaseUrl() +"VocabimateContentServer/webapi/video/stream?videoId=" + model.getVideoId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginButtonTextUpdate();
        makeVocaDirectory();
//        File dir = new File(storageDirectoryZips + videoId);
//        if(isFolderPresent(dir)){
//            iVDownload.setImageResource(R.mipmap.ic_download_complete);
//        } else {
//            iVDownload.setImageResource(R.mipmap.ic_download);
//        }
    }

    private void makeVocaDirectory() {
        File file = new File(Utils.getStorageDirectoryZips());
        File fileExtracts = new File(Utils.getStorageDirectoryExtracts());
        if(!file.exists()) {
            file.mkdirs();
        }
        if(!fileExtracts.exists()) {
            fileExtracts.mkdirs();
        }
    }

    private void loginButtonTextUpdate() {
        if(!TextUtils.isEmpty(TokenManager.getToken())){
            btnLogin.setText("Logout");
        } else {
            btnLogin.setText("Login");
        }
    }
}
