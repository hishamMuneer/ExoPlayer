package com.google.android.exoplayer2.source.hls;


import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.Key;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by Hisham on 05/Sep/2017 - 18:02
 * USE: compile 'commons-io:commons-io:2.5'
 */

public class KeyWriter {

    private static final String keyStoragePath = Environment.getExternalStorageDirectory() + "/voca/keys/";
    private static final String TAG = KeyWriter.class.getSimpleName();
    private static final String ALGO = "AES";
    private static final byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't',
            'S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };


    public static byte[] readByteToFileUnencryptedData(String serverFileUrl, File tempKeyPath) {
        try {
            tempKeyPath.mkdirs();
            String videoId = getVideoIdFromUrl(serverFileUrl);
            if (videoId != null) {
                return FileUtils.readFileToByteArray(new File(tempKeyPath.getAbsolutePath())); // decrypting
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] readByteToFileEncryptedData(String serverFileUrl) {
        try {
            File keyFile = new File(keyStoragePath);
            keyFile.mkdirs();
            String videoId = getVideoIdFromUrl(serverFileUrl);
            if (videoId != null) {
                byte[] decrypt = decrypt(FileUtils.readFileToByteArray(new File(keyFile.getAbsolutePath() + "/" + videoId)));
                Log.d(TAG, "readByteToFileEncryptedData: read key from file: " + Arrays.toString(decrypt));
                return decrypt; // decrypting
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeByteToFile(byte[] keyBytes, String serverFileUrl) {
        try {
            File keyFile = new File(keyStoragePath);
            keyFile.mkdirs();
            String videoId = getVideoIdFromUrl(serverFileUrl);
            if (videoId != null){
                byte[] encrypt = encrypt(keyBytes);
                if (encrypt != null) {
                    FileUtils.writeByteArrayToFile(new File(keyFile.getAbsolutePath() + "/" + videoId), encrypt);
                    Log.d(TAG, "writeByteToFile: key written: " + Arrays.toString(keyBytes));
                } else {
                    Log.e(TAG, "writeByteToFile: after encryption data was null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getVideoIdFromUrl(String serverFileUrl) {
        String videoId = null;
        try {
            Map<String, String> params = splitQuery(new URL(serverFileUrl));
            if (params != null && params.size() > 0 && params.containsKey("videoId")) {
                videoId = params.get("videoId");
            }
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        }
        return videoId;
    }


    private static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    private static byte[] encrypt(byte[] data) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            //String encryptedValue = new BASE64Encoder().encode(encVal);
            return c.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] decrypt(byte[] encryptedData) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            return c.doFinal(encryptedData);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGO);
    }

}
