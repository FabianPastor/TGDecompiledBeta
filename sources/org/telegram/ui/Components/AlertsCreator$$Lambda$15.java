package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class AlertsCreator$$Lambda$15 implements OnClickListener {
    private final boolean arg$1;
    private final boolean arg$2;
    private final long arg$3;
    private final Runnable arg$4;

    AlertsCreator$$Lambda$15(boolean z, boolean z2, long j, Runnable runnable) {
        this.arg$1 = z;
        this.arg$2 = z2;
        this.arg$3 = j;
        this.arg$4 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createColorSelectDialog$16$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, dialogInterface, i);
    }
}
