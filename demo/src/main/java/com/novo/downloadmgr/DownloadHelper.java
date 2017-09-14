package com.novo.downloadmgr;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.novo.models.FileDownloadModel;
import com.novo.util.Utils;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.novo.util.Utils.TAG;

/**
 * Created by Hisham on 12/Sep/2017 - 13:31
 */

public class DownloadHelper {

    private final Context context;
    private DownloadManager downloadManager;

    public DownloadHelper(Context context) {
        this.context = context;
        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
    }

    public long downloadFiles(FileDownloadModel downloadModel, View v) {

        long downloadReference;

        // Create request for android download manager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadModel.getLink()));

        //Setting title of request
        request.setTitle(downloadModel.getVideoTitle());

        //Setting description of request
//        request.setDescription(downloadModel.getVideoTitle());

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory
//        if(v.getId() == R.id.DownloadMusic)
        request.setDestinationInExternalFilesDir(context, new Utils(context).getPathForDownloadManager(), downloadModel.getVideoId());
//        else if(v.getId() == R.id.DownloadImage)
//            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,"AndroidTutorialPoint.jpg");

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

//        Button downloadStatus = (Button) findViewById(R.id.downloadStatus);
//        downloadStatus.setEnabled(true);
//        Button CancelDownload = (Button) findViewById(R.id.CancelDownload);
//        CancelDownload.setEnabled(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.addRequestHeader("token", token) // todo will be used later

        return downloadReference;
    }

    public void checkDownloadStatus(long downloadReference) {

        DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        ImageDownloadQuery.setFilterById(downloadReference);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(ImageDownloadQuery);
        if (cursor.moveToFirst()) {
            downloadStatus(cursor, downloadReference);
        }
    }

    private void downloadStatus(Cursor cursor, long downloadReference) {

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        int sizeIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
        int downloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
        long size = cursor.getInt(sizeIndex);
        long downloaded = cursor.getInt(downloadedIndex);
        double progress = 0.0;
        if (size != -1) progress = downloaded * 100.0 / size;
        // At this point you have the progress as a percentage.

        String statusText = "";
        String reasonText = "";

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }

        Log.d(TAG, "downloadStatus: " + "Music Download Status:" + "\n" + statusText + "\n" +
                reasonText);

//        if(downloadReference == Music_DownloadId) {
//
//            Toast toast = Toast.makeText(MainActivity.this,
//                    "Music Download Status:" + "\n" + statusText + "\n" +
//                            reasonText,
//                    Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.TOP, 25, 400);
//            toast.show();
//
//        }
//        else {
//
//            Toast toast = Toast.makeText(MainActivity.this,
//                    "Image Download Status:"+ "\n" + statusText + "\n" +
//                            reasonText,
//                    Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.TOP, 25, 400);
//            toast.show();
//
//            // Make a delay of 3 seconds so that next toast (Music Status) will not merge with this one.
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                }
//            }, 3000);
//        }
        cursor.close();
    }

    public void deleteFile(long fileId) {
        downloadManager.remove(fileId);
    }

}
