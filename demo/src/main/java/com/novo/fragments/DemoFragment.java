package com.novo.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.source.hls.HLSUtils;
import com.google.android.exoplayer2.source.hls.KeyWriter;
import com.google.android.exoplayer2.upstream.novo.TokenManager;
import com.google.gson.Gson;
import com.novo.R;
import com.novo.adapters.VideoAdapter;
import com.novo.main.PlayerActivity;
import com.novo.models.FileDownloadModel;
import com.novo.models.VideoModel;
import com.novo.network.DownloadTask;
import com.novo.network.EndPoints;
import com.novo.network.ServerHit;
import com.novo.network.ZipHelper;
import com.novo.services.DownloaderService;
import com.novo.util.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.novo.util.Utils.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDemoFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DemoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DemoFragment extends Fragment implements VideoAdapter.ItemListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnDemoFragInteractionListener mListener;


    private GridView lvAll;
    private Activity activity;
    private BroadcastReceiver receiver;
    private List<VideoModel> videoModelsList;
    private VideoAdapter adapter;

    public DemoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DemoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DemoFragment newInstance(String param1, String param2) {
        DemoFragment fragment = new DemoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demo, container, false);
        initStuff(view);

        setReceiver();

        return view;
    }

    private void setReceiver() {

        // register a receiver for callbacks
        IntentFilter filter = new IntentFilter("progress_callback");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long startTime = System.currentTimeMillis();
                //do something based on the intent's action
                Bundle b = intent.getExtras();
                FileDownloadModel downloadModel = (FileDownloadModel) b.getSerializable("fileDownloadModelReturned");
                // could be used to update a progress bar or show info somewhere in the Activity
                Log.d(TAG, "onReceive: PROGRESS HOME: " + downloadModel.getProgress() + "%");

                for (int i = 0; i < lvAll.getAdapter().getCount(); i++) {
                    View child = lvAll.getChildAt(i);
                    if (child != null && videoModelsList.get(i).getVideoId().equalsIgnoreCase(downloadModel.getVideoId())) {
                        TextView tvPercentage = (TextView) child.findViewById(R.id.tvPercentage); // todo fix getting a crash here sometimes null pointer
                        ImageView ivDownload = (ImageView) child.findViewById(R.id.ivDownload);

                        switch (downloadModel.getStatus()) {
                            case DOWNLOADING:
                                if (downloadModel.getProgress() == -1) {
                                    tvPercentage.setText(R.string.downloading);
                                } else {
                                    tvPercentage.setText(downloadModel.getProgress() + "%");
                                }
                                break;
                            case DOWNLOADED:
                                tvPercentage.setText(R.string.completed);
                                break;
                            case UNZIPPING:
                                tvPercentage.setText(R.string.processing);
                                break;
                            case UNZIPPED:
                                tvPercentage.setText("");
                                ivDownload.setImageResource(R.drawable.ic_delete_black_24dp);
                                break;
                            case ERROR:
                                tvPercentage.setText(R.string.unable_to_download);
                                break;
                            case CANCELLED:
                                tvPercentage.setText("");
                                break;
                        }
                        break;
                    }
                }
                Log.d(TAG, "onReceive: Time taken to search views in videoModelsList = " + (System.currentTimeMillis() - startTime) + " ms");
            }
        };
        activity.registerReceiver(receiver, filter);

    }

    private void initStuff(View view) {

        lvAll = (GridView) view.findViewById(R.id.lvAll);
        ServerHit.JSONTask task = new ServerHit.JSONTask(activity, TokenManager.getToken(), "GET", null, null, new ServerHit.ServiceHitResponseListener() {
            @Override
            public void onDone(String response) {
                Log.d(TAG, "onDone: " + response);
                response = "[\n" +
                        "  {\n" +
                        "    \"thumbnail\": \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",\n" +
                        "    \"name\": \"Encrypted Stream - Open Policy\",\n" +
                        "    \"videoId\": \"Gear_640x3642340_750k_open\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"thumbnail\": \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",\n" +
                        "    \"name\": \"Encrypted Stream - Token Auth policy\",\n" +
                        "    \"videoId\": \"Gear_640x3612340_750k_auth\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"thumbnail\": \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",\n" +
                        "    \"name\": \"Gear\",\n" +
                        "    \"videoId\": \"5WT9g212m4outw\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"thumbnail\": \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",\n" +
                        "    \"name\": \"Encrypted Stream - Open Policy\",\n" +
                        "    \"videoId\": \"Gear_640x369870_750k_open\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"thumbnail\": \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",\n" +
                        "    \"name\": \"Encrypted Stream - Token Auth policy\",\n" +
                        "    \"videoId\": \"Gear_640x8360_750k_auth\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"thumbnail\": \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",\n" +
                        "    \"name\": \"Gear\",\n" +
                        "    \"videoId\": \"5WT9gm654outw\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"thumbnail\": \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",\n" +
                        "    \"name\": \"Encrypted Stream - Open Policy\",\n" +
                        "    \"videoId\": \"Gear_54640x360_750k_open\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"thumbnail\": \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",\n" +
                        "    \"name\": \"Encrypted Stream - Token Auth policy\",\n" +
                        "    \"videoId\": \"Gear_63440x360_750k_auth\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"thumbnail\": \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",\n" +
                        "    \"name\": \"Gear\",\n" +
                        "    \"videoId\": \"5WT9gm234outw\"\n" +
                        "  }\n" +
                        "]";
                videoModelsList = getVideoModelsFromResponse(response);
                adapter = new VideoAdapter(activity, R.layout.row_videos_grid, videoModelsList);
                adapter.setItemListener(DemoFragment.this);
                lvAll.setAdapter(adapter);


            }

            @Override
            public void onError(String error) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String response = "[ {  \"thumbnail\" : \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",  \"name\" : \"Encrypted Stream - Open Policy\",  \"videoId\" : \"Gear_640x360_750k_open\"}, {  \"thumbnail\" : \"http://35.154.11.202/VocabimateContentServer/thumbnails/thumbnail.jpg\",  \"name\" : \"Encrypted Stream - Token Auth policy\",  \"videoId\" : \"Gear_640x360_750k_auth\"} ]";
                        onDone(response);
                    }
                });


            }
        });

        String url = EndPoints.getBaseUrl() + "VocabimateContentServer/webapi/video/fetchAll";
        task.execute(url);

    }


    private void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @NonNull
    private List<VideoModel> getVideoModelsFromResponse(String response) {
        List<VideoModel> items = new ArrayList<>();
        if (TextUtils.isEmpty(response)) {
            return items;
        }
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                VideoModel model = new Gson().fromJson(jsonObject.toString(), VideoModel.class);
                items.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void playMediaFromServer(VideoModel model) {
        ServerHit.JSONTask streamTask = new ServerHit.JSONTask(activity, TokenManager.getToken(), "GET", null, null, new ServerHit.ServiceHitResponseListener() {
            @Override
            public void onDone(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String videoUrl = object.getString("videoUrl");
                    // send to player
                    Intent intent = new Intent(activity, PlayerActivity.class);
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
        streamTask.execute(EndPoints.getBaseUrl() + "VocabimateContentServer/webapi/video/stream?videoId=" + model.getVideoId());
    }


    @Override
    public void onVideoPlayClicked(final VideoModel model) {
        File dir = new File(new Utils(activity).getStorageDirectoryExtracts() + model.getVideoId());
        if (Utils.isFolderPresent(dir)) {
            File[] file = dir.listFiles();
            // trying to find my file
            Log.d(TAG, "onVideoPlayClicked: " + ZipHelper.searchFile(file, null));
            ZipHelper.searchFile(file, new ZipHelper.FileListener() {
                @Override
                public void onFileSearchComplete(boolean fileFound, String fileToPlay) {
                    if (fileFound && !TextUtils.isEmpty(fileToPlay)) {
                        Intent intent = new Intent(activity, PlayerActivity.class);
                        intent.setData(Uri.parse(fileToPlay));
                        intent.setAction(PlayerActivity.ACTION_VIEW);
                        startActivity(intent);
                    } else {
                        Toast.makeText(activity, "Unable to play local video, playing stream.", Toast.LENGTH_SHORT).show();
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
        String serverFileUrl = EndPoints.getBaseUrl() + "VocabimateContentServer/webapi/video/download?videoId=" + model.getVideoId();
        // todo problem with zip file, hardcoded
        final String keyFileUrl = EndPoints.getBaseUrl() + "VocabimateKeyServer/webapi/keys/getKey?videoId=" + model.getVideoId();
        String videoId = HLSUtils.getVideoIdFromUrl(serverFileUrl);
        if(TextUtils.isEmpty(videoId)){
            Toast.makeText(activity, "Video id not found", Toast.LENGTH_SHORT).show();
            return;
        }
        // execute this when the downloader must be fired
        final File sourceZipFile = new File(new Utils(activity).getStorageDirectoryZips() + videoId);
        String fileNameWithOutExt = FilenameUtils.removeExtension(sourceZipFile.getName());
        final File targetDirectory = new File(new Utils(activity).getStorageDirectoryExtracts() + fileNameWithOutExt);
        targetDirectory.mkdir();



        FileDownloadModel fileDownloadModel = new FileDownloadModel()
                .setVideoId(model.getVideoId())
                .setVideoTitle(model.getName())
                .setToken(TokenManager.getToken())
                .setLink("https://drmdemo-94ea7.firebaseapp.com/arc.zip") // todo hard code
//                .setLink(serverFileUrl)
                .setFilePath(sourceZipFile.getAbsolutePath())
                .setTargetDirectoryPath(targetDirectory.getAbsolutePath())
                .setCallBackIntent("progress_callback");

        Intent intent = new Intent(activity, DownloaderService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("fileDownloadModel", fileDownloadModel);
//        bundle.putString("videoId", model.getVideoId());
//        bundle.putString("videoTitle", model.getName());
//        bundle.putString("token", TokenManager.getToken());
//        bundle.putString("link", "https://drmdemo-94ea7.firebaseapp.com/arc.zip");
//        bundle.putString("filePath", sourceZipFile.getAbsolutePath());
//        bundle.putString("targetDirectoryPath", targetDirectory.getAbsolutePath());
//        bundle.putString("CallbackString", "progress_callback");
        activity.startService(intent.putExtras(bundle)); // todo uncomment

//        final DownloadTask downloadTask = new DownloadTask(activity, TokenManager.getToken(), sourceZipFile.getAbsolutePath(), new DownloadTask.DownloadTaskListener() {
//            @Override
//            public void onFileDownload() {
//                new ZipHelper.ZipTask(activity, new ZipHelper.ZipTaskListener() {
//                    @Override
//                    public void onUnzipped(String fileToPlay) {
//                        Log.d(TAG, "onUnzipped: " + fileToPlay);
//                        ivDownload.setImageResource(R.mipmap.ic_download_complete);
//                    }
//                }).execute(sourceZipFile, targetDirectory);
//            }
//        });
//        downloadTask.execute(serverFileUrl);
//
        final File tempKeyFile = new File(new Utils(activity).getTempDirectoryExtracts() + videoId);
        final DownloadTask keyTask = new DownloadTask(activity, "KEY", TokenManager.getToken(), tempKeyFile.getAbsolutePath(), new DownloadTask.DownloadTaskListener() {
            @Override
            public void onFileDownload() {
                KeyWriter writer = new KeyWriter(activity);
                writer.writeByteToFile(writer.readByteToFileUnencryptedData(keyFileUrl, tempKeyFile), keyFileUrl);
                writer.deleteTempKey(tempKeyFile);
            }
        });
        keyTask.execute(keyFileUrl);
    }

    @Override
    public void onDeleteClicked(File directory, ImageView ivDownload) {
        try {
            FileUtils.deleteDirectory(directory);
            ivDownload.setImageResource(R.drawable.ic_file_download_black_24dp);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onDeleteClicked: unable to delete directory" + e.getLocalizedMessage());
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDemoFragInteractionListener) {
            mListener = (OnDemoFragInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDemoFragInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDemoFragInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
    }
}
