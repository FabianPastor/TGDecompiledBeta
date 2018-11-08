package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$2 */
final /* synthetic */ class AlertsCreator$$Lambda$2 implements OnClickListener {
    private final Runnable arg$1;

    AlertsCreator$$Lambda$2(Runnable runnable) {
        this.arg$1 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$showSecretLocationAlert$2$AlertsCreator(this.arg$1, dialogInterface, i);
    }
}
