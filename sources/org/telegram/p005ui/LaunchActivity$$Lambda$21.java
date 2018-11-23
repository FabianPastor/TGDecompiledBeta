package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$21 */
final /* synthetic */ class LaunchActivity$$Lambda$21 implements OnClickListener {
    private final int arg$1;

    LaunchActivity$$Lambda$21(int i) {
        this.arg$1 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        LaunchActivity.lambda$didReceivedNotification$34$LaunchActivity(this.arg$1, dialogInterface, i);
    }
}
