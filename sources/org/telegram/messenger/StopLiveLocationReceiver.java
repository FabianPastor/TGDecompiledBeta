package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopLiveLocationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        for (int a = 0; a < 3; a++) {
            LocationController.getInstance(a).removeAllLocationSharings();
        }
    }
}
