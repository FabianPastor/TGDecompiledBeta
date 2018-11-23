package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$18 */
final /* synthetic */ class LaunchActivity$$Lambda$18 implements OnClickListener {
    private final LaunchActivity arg$1;

    LaunchActivity$$Lambda$18(LaunchActivity launchActivity) {
        this.arg$1 = launchActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onRequestPermissionsResult$31$LaunchActivity(dialogInterface, i);
    }
}
