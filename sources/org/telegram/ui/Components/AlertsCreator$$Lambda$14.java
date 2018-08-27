package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class AlertsCreator$$Lambda$14 implements OnClickListener {
    private final boolean arg$1;
    private final int[] arg$2;
    private final boolean arg$3;
    private final long arg$4;
    private final Runnable arg$5;

    AlertsCreator$$Lambda$14(boolean z, int[] iArr, boolean z2, long j, Runnable runnable) {
        this.arg$1 = z;
        this.arg$2 = iArr;
        this.arg$3 = z2;
        this.arg$4 = j;
        this.arg$5 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createColorSelectDialog$15$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, dialogInterface, i);
    }
}
