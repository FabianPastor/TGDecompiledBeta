package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$15 */
final /* synthetic */ class LaunchActivity$$Lambda$15 implements OnClickListener {
    private final int arg$1;
    private final int[] arg$2;

    LaunchActivity$$Lambda$15(int i, int[] iArr) {
        this.arg$1 = i;
        this.arg$2 = iArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        LaunchActivity.lambda$runLinkRequest$27$LaunchActivity(this.arg$1, this.arg$2, dialogInterface, i);
    }
}
