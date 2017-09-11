package com.novo.main;

import android.app.Activity;

import com.google.android.exoplayer2.source.hls.KeyWriter;
import com.google.android.exoplayer2.upstream.novo.TokenManager;
import com.novo.util.Utils;

/**
 * Created by Hisham on 11/Sep/2017 - 20:44
 */

public class LogoutHelper {
    public static void cleanup(Activity activity) {
        TokenManager.setToken(null);
        Utils utils = new Utils(activity);
        utils.deleteAllDirectories();
        KeyWriter writer = new KeyWriter(activity);
        writer.deleteAllKeys();
    }
}
