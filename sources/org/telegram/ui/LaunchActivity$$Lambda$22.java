package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class LaunchActivity$$Lambda$22 implements OnClickListener {
    private final int arg$1;

    LaunchActivity$$Lambda$22(int i) {
        this.arg$1 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        LaunchActivity.lambda$didReceivedNotification$36$LaunchActivity(this.arg$1, dialogInterface, i);
    }
}
