package com.google.android.exoplayer2.upstream.novo;

/**
 * Created by Hisham on 8/28/2017.
 */

public class TokenManager {

    private static String token = null;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        TokenManager.token = token;
    }
}
