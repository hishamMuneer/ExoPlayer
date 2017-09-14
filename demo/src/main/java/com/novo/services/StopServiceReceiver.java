package com.novo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Hisham on 12/Sep/2017 - 20:32
 */

public class StopServiceReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, DownloaderService.class);
        context.stopService(service);
    }
}
