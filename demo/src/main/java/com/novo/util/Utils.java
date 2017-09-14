package com.novo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.novo.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Hisham on 01/Sep/2017 - 16:49
 */

public class Utils {

    private final Context context;
    private final String pathForDownloadManager = "/voca/zips/";

    public Utils(Context context) {
        this.context = context;

        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir == null) {
            Log.e(TAG, "Utils: externalFilesDir is null.");
            storageDirectoryZips = null;
            storageDirectoryExtracts = null;
            tempDirectoryExtracts = null;
        } else {
            String vocaRoot = externalFilesDir.getAbsolutePath() + "/voca/";
            storageDirectoryZips = vocaRoot + "zips/";
            storageDirectoryExtracts = vocaRoot + "extracts/";
            tempDirectoryExtracts = vocaRoot + "temp/";
            File tempDirectory = new File(tempDirectoryExtracts);
            if (!tempDirectory.exists()) {
                tempDirectory.mkdirs();
            }

            File fileZips = new File(storageDirectoryZips);
            if (!fileZips.exists()) {
                fileZips.mkdirs();
            }

            File fileExtracts = new File(storageDirectoryExtracts);
            if (!fileExtracts.exists()) {
                fileExtracts.mkdirs();
            }
        }
    }

    private final String storageDirectoryZips;
    //    private final String storageDirectoryZips =Environment.getExternalStorageDirectory() + "/voca/zips/";
    private final String storageDirectoryExtracts;
    //    private final String storageDirectoryExtracts = Environment.getExternalStorageDirectory() + "/voca/extracts/";
    private final String tempDirectoryExtracts;
    //    private final String tempDirectoryExtracts = Environment.getExternalStorageDirectory() + "/voca/temp/";
    public static final String TAG = "Novo";

    public String getTempDirectoryExtracts() {
        return tempDirectoryExtracts;
    }

    public String getStorageDirectoryZips() {
        return storageDirectoryZips;
    }

    public String getStorageDirectoryExtracts() {
        return storageDirectoryExtracts;
    }


    public static boolean isFolderPresent(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public void deleteAllDirectories() {
        try {
            File tempDirectory = new File(tempDirectoryExtracts);
            File fileZips = new File(storageDirectoryZips);
            File fileExtracts = new File(storageDirectoryExtracts);
            FileUtils.deleteDirectory(tempDirectory);
            FileUtils.deleteDirectory(fileZips);
            FileUtils.deleteDirectory(fileExtracts);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onDeleteClicked: unable to delete directory" + e.getLocalizedMessage());
        }

    }

    public String getPathForDownloadManager() {
        return pathForDownloadManager;
    }
}
