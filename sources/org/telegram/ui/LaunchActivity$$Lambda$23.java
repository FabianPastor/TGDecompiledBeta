package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class LaunchActivity$$Lambda$23 implements OnClickListener {
    private final LaunchActivity arg$1;

    LaunchActivity$$Lambda$23(LaunchActivity launchActivity) {
        this.arg$1 = launchActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didReceivedNotification$37$LaunchActivity(dialogInterface, i);
    }
}
