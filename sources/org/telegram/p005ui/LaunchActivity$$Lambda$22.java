package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$22 */
final /* synthetic */ class LaunchActivity$$Lambda$22 implements OnClickListener {
    private final LaunchActivity arg$1;

    LaunchActivity$$Lambda$22(LaunchActivity launchActivity) {
        this.arg$1 = launchActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didReceivedNotification$35$LaunchActivity(dialogInterface, i);
    }
}
