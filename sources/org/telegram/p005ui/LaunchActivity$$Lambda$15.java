package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.tgnet.ConnectionsManager;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$15 */
final /* synthetic */ class LaunchActivity$$Lambda$15 implements OnCancelListener {
    private final int arg$1;
    private final int[] arg$2;

    LaunchActivity$$Lambda$15(int i, int[] iArr) {
        this.arg$1 = i;
        this.arg$2 = iArr;
    }

    public void onCancel(DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.arg$1).cancelRequest(this.arg$2[0], true);
    }
}
