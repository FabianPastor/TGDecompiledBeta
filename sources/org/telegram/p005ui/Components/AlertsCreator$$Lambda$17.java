package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$17 */
final /* synthetic */ class AlertsCreator$$Lambda$17 implements OnClickListener {
    private final long arg$1;
    private final int arg$2;
    private final Runnable arg$3;

    AlertsCreator$$Lambda$17(long j, int i, Runnable runnable) {
        this.arg$1 = j;
        this.arg$2 = i;
        this.arg$3 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createColorSelectDialog$18$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, dialogInterface, i);
    }
}
