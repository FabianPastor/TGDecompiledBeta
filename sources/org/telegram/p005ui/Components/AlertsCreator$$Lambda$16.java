package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$16 */
final /* synthetic */ class AlertsCreator$$Lambda$16 implements OnClickListener {
    private final long arg$1;
    private final int[] arg$2;
    private final int arg$3;
    private final Runnable arg$4;

    AlertsCreator$$Lambda$16(long j, int[] iArr, int i, Runnable runnable) {
        this.arg$1 = j;
        this.arg$2 = iArr;
        this.arg$3 = i;
        this.arg$4 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createColorSelectDialog$17$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, dialogInterface, i);
    }
}
