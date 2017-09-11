package com.google.android.exoplayer2.source.hls;


import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by Hisham on 05/Sep/2017 - 18:02
 * USE: compile 'commons-io:commons-io:2.5'
 */

public class KeyWriter {

    private final Context context;

    public KeyWriter(Context context) {
        this.context = context;
        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir == null) {
            keyStoragePath = null;
        } else {
            keyStoragePath = externalFilesDir.getAbsolutePath() + "/voca/keys/";
        }
    }

    private final String keyStoragePath;//  = Environment.getExternalStorageDirectory() + "/voca/keys/";
    private static final String TAG = KeyWriter.class.getSimpleName();
    private static final String ALGO = "AES";
    private static final byte[] keyValue = new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't',
            'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};


    public byte[] readByteToFileUnencryptedData(String serverFileUrl, File tempKeyPath) {
        try {
            tempKeyPath.mkdirs();
            String videoId = HLSUtils.getVideoIdFromUrl(serverFileUrl);
            if (videoId != null) {
                return FileUtils.readFileToByteArray(new File(tempKeyPath.getAbsolutePath())); // decrypting
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public byte[] readByteToFileEncryptedData(String serverFileUrl) {
        try {
            if(keyStoragePath != null) {
                File keyFile = new File(keyStoragePath);
                keyFile.mkdirs();
                String videoId = HLSUtils.getVideoIdFromUrl(serverFileUrl);
                if (videoId != null) {
                    byte[] decrypt = decrypt(FileUtils.readFileToByteArray(new File(keyFile.getAbsolutePath() + "/" + videoId)));
                    Log.d(TAG, "readByteToFileEncryptedData: read key from file: " + Arrays.toString(decrypt));
                    return decrypt; // decrypting
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeByteToFile(byte[] keyBytes, String serverFileUrl) {
        try {
            if(keyStoragePath != null) {
                File keyFile = new File(keyStoragePath);
                keyFile.mkdirs();
                String videoId = HLSUtils.getVideoIdFromUrl(serverFileUrl);
                if (videoId != null) {
                    byte[] encrypt = encrypt(keyBytes);
                    if (encrypt != null) {
                        FileUtils.writeByteArrayToFile(new File(keyFile.getAbsolutePath() + "/" + videoId), encrypt);
                        Log.d(TAG, "writeByteToFile: key written: " + Arrays.toString(keyBytes));
                    } else {
                        Log.e(TAG, "writeByteToFile: after encryption data was null");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private byte[] encrypt(byte[] data) {
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

    private byte[] decrypt(byte[] encryptedData) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            return c.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Key generateKey() throws Exception {
        return new SecretKeySpec(keyValue, ALGO);
    }

    public void deleteTempKey(File tempKeyFile) {
        if(!BuildConfig.DEBUG) { // if not debugging - delete zip file
            String absolutePath = tempKeyFile.getAbsolutePath();
            if (tempKeyFile.delete()) {
                Log.d(TAG, "zip file deleted: " + absolutePath);
            } else {
                Log.d(TAG, "Error deleting zip file : " + absolutePath);
            }
        }
    }

    public void deleteAllKeys() {
        try {
            File keyDirectory = new File(keyStoragePath);
            FileUtils.deleteDirectory(keyDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onDeleteClicked: unable to delete directory" + e.getLocalizedMessage());
        }
    }
}
