package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.HashMap;

final /* synthetic */ class LaunchActivity$$Lambda$22 implements OnClickListener {
    private final LaunchActivity arg$1;
    private final HashMap arg$2;
    private final int arg$3;

    LaunchActivity$$Lambda$22(LaunchActivity launchActivity, HashMap hashMap, int i) {
        this.arg$1 = launchActivity;
        this.arg$2 = hashMap;
        this.arg$3 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didReceivedNotification$35$LaunchActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
