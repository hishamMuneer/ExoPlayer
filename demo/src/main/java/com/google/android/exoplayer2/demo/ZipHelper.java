package com.google.android.exoplayer2.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Hisham on 31/Aug/2017 - 16:06 - https://stackoverflow.com/a/27050680
 */

public class ZipHelper {

    public static class ZipTask extends AsyncTask<File, Object, File> {

        private final Activity activity;
        private final ZipTaskListener listener;

        public ZipTask(Activity activity, ZipTaskListener listener) {
            this.activity = activity;
            this.listener = listener;
        }

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Unzipping, please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected File doInBackground(File... params) {
            try {
                ZipHelper.unzip(params[0], params[1]);
                return params[1];
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(File targetDirectory) {
            super.onPostExecute(targetDirectory);
            progressDialog.dismiss();
            listener.onUnzipped(targetDirectory.getAbsolutePath());
        }
    }


    public interface ZipTaskListener {
        void onUnzipped(String fileToPlay);
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }
    }

    /**
     * This method searches the first file named
     * @param file
     * @param listener
     * @return
     */
    public static boolean searchFile(File[] file, FileListener listener) {
        if(file.length > 0) {
            for (File f : file) {
                String filenameToSearch = "prog_index.m3u8"; // file name to search in the folder you just unarchived
//                if (f.isFile() && f.getName().equalsIgnoreCase(filenameToSearch)) {// f.getPath().endsWith("master.m3u8")) {
                if (f.isFile() && f.getPath().endsWith(filenameToSearch)) {
                    if(listener != null){ listener.onFileSearchComplete(true, f.getAbsolutePath());}
                    Log.d(TAG, "searchFile: " + f.getAbsolutePath());
                    return true; // if the file is found on the root level, we stop the execution
                }
            }

            for (File f : file) {
                if (f.isDirectory() && searchFile(f.listFiles(), listener)) {
                    return true;
                }
            }
        }
        if(listener != null){ listener.onFileSearchComplete(false, null);}
        return false;
    }

    private static final String TAG = ZipHelper.class.getSimpleName();

    public interface FileListener{
        void onFileSearchComplete(boolean fileFound, String path);
    }
}
