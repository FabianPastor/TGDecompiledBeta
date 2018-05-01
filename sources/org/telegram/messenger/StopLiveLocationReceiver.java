package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopLiveLocationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        for (context = null; context < 3; context++) {
            LocationController.getInstance(context).removeAllLocationSharings();
        }
    }
}
