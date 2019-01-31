package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class LaunchActivity$$Lambda$23 implements OnClickListener {
    private final int arg$1;

    LaunchActivity$$Lambda$23(int i) {
        this.arg$1 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        LaunchActivity.lambda$didReceivedNotification$37$LaunchActivity(this.arg$1, dialogInterface, i);
    }
}
