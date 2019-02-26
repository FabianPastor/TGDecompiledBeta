package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class AlertsCreator$$Lambda$21 implements OnClickListener {
    private final long arg$1;
    private final Runnable arg$2;

    AlertsCreator$$Lambda$21(long j, Runnable runnable) {
        this.arg$1 = j;
        this.arg$2 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createColorSelectDialog$24$AlertsCreator(this.arg$1, this.arg$2, dialogInterface, i);
    }
}
