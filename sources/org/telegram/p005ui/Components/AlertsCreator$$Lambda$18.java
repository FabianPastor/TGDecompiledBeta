package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$18 */
final /* synthetic */ class AlertsCreator$$Lambda$18 implements OnClickListener {
    private final long arg$1;
    private final Runnable arg$2;

    AlertsCreator$$Lambda$18(long j, Runnable runnable) {
        this.arg$1 = j;
        this.arg$2 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createColorSelectDialog$19$AlertsCreator(this.arg$1, this.arg$2, dialogInterface, i);
    }
}
