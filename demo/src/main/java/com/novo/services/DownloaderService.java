package com.novo.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer2.upstream.novo.TokenManager;
import com.novo.BuildConfig;
import com.novo.R;
import com.novo.main.HomeActivity;
import com.novo.models.FileDownloadModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.novo.util.Utils.TAG;

public class DownloaderService extends Service {

    private NotificationCompat.Builder builder;

    public DownloaderService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Downloading...")
                .setContentIntent(pendingIntent);
//        Notification notification = builder.build();
//        startForeground(1337, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("fileDownloadModel")) {
            final FileDownloadModel fileDownloadModel = (FileDownloadModel) intent.getSerializableExtra("fileDownloadModel");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startDownload(fileDownloadModel, builder);
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String startDownload(FileDownloadModel fileDownloadModel, NotificationCompat.Builder builder) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(fileDownloadModel.getLink());
            connection = (HttpURLConnection) url.openConnection();
            if (!TextUtils.isEmpty(fileDownloadModel.getToken())) {
                connection.setRequestProperty("token", TokenManager.getToken());
            }

            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(fileDownloadModel.getFilePath());

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            int updateCounter = 0;
            while ((count = input.read(data)) != -1) {
//                // allow canceling with back button
//                if (isCancelled()) {
//                    input.close();
//                    return null;
//                }
                total += count;
                updateCounter++; // counter for updating progress
                if (updateCounter % 200 == 0) {
                    updateProgressInNotification(fileDownloadModel.setStatus(FileDownloadModel.Status.DOWNLOADING), builder, fileLength, total);
                }
                output.write(data, 0, count);
            }

            updateProgressInNotification(fileDownloadModel.setStatus(FileDownloadModel.Status.DOWNLOADED).setProgress(100), builder, fileLength, total);

            // unzipping
            builder.setContentText("Unzipping");
            updateProgressInNotification(fileDownloadModel.setStatus(FileDownloadModel.Status.UNZIPPING), builder, fileLength, total);
            unzip(new File(fileDownloadModel.getFilePath()), new File(fileDownloadModel.getTargetDirectoryPath()));
            updateProgressInNotification(fileDownloadModel.setStatus(FileDownloadModel.Status.UNZIPPED), builder, fileLength, total);

        } catch (Exception e) {
            sendCallback(fileDownloadModel.setProgress(-1).setStatus(FileDownloadModel.Status.ERROR));
            return e.toString();
        } finally {

            // When the loop is finished, updates the notification
            builder.setContentText("Download complete");
            // Removes the progress bar
//                    builder.setProgress(0,0,false);
            stopSelf();
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    private void unzip(File zipFile, File targetDirectory) throws IOException {
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
            if(!BuildConfig.DEBUG) { // if not debugging - delete zip file
                String absolutePath = zipFile.getAbsolutePath();
                if (zipFile.delete()) {
                    Log.d(TAG, "zip file deleted: " + absolutePath);
                } else {
                    Log.d(TAG, "Error deleting zip file : " + absolutePath);
                }
            }
        }
    }

    int updateCounter = 0;

    private void updateProgressInNotification(FileDownloadModel fileDownloadModel, NotificationCompat.Builder builder, int fileLength, long total) {
        Log.d("counter", "startDownload: " + ++updateCounter);
        builder.setContentTitle(fileDownloadModel.getVideoTitle());
        // publishing the progress....
        int percentage = -1;
        if (fileLength > 0) {// only if total length is known
            percentage = (int) (total * 100 / fileLength);
            long totalMB = fileLength / (1024 * 1024);
            long currentMB = total / (1024 * 1024);
            builder.setProgress(100, percentage, false);
            builder.setContentText(percentage + "%")
                    .setContentInfo(currentMB + "MB / " + totalMB + "MB");
        } else {
            builder.setProgress(100, 0, true);
        }
        fileDownloadModel.setProgress(percentage);
        sendCallback(fileDownloadModel);
        startForeground(1337, builder.build());
    }

    private void sendCallback(FileDownloadModel fileDownloadModel) {
        Intent i = new Intent(fileDownloadModel.getCallBackIntent());
        i.putExtra("fileDownloadModelReturned", fileDownloadModel);
        sendBroadcast(i);
    }


}
