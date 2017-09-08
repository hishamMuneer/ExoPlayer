package com.novo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hisham on 01/Sep/2017 - 16:49
 */

public class Utils {

    private static final String storageDirectoryZips = Environment.getExternalStorageDirectory() + "/voca/zips/";
    private static final String storageDirectoryExtracts = Environment.getExternalStorageDirectory() + "/voca/extracts/";
    private static final String tempDirectoryExtracts = Environment.getExternalStorageDirectory() + "/voca/temp/";
    public static final String TAG = "Novo";

    public static String getTempDirectoryExtracts() {
        File tempDirectory = new File(tempDirectoryExtracts);
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }
        return tempDirectoryExtracts;
    }

    public static String getStorageDirectoryZips() {
        File fileZips = new File(storageDirectoryZips);
        if (!fileZips.exists()) {
            fileZips.mkdirs();
        }
        return storageDirectoryZips;
    }

    public static String getStorageDirectoryExtracts() {
        File fileExtracts = new File(storageDirectoryExtracts);
        if (!fileExtracts.exists()) {
            fileExtracts.mkdirs();
        }
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
    private Utils() { }

}
