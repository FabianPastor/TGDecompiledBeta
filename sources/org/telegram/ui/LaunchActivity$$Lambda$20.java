package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class LaunchActivity$$Lambda$20 implements OnClickListener {
    private final int arg$1;

    LaunchActivity$$Lambda$20(int i) {
        this.arg$1 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        LaunchActivity.lambda$didReceivedNotification$32$LaunchActivity(this.arg$1, dialogInterface, i);
    }
}
