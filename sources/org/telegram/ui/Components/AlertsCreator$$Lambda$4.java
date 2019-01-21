package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class AlertsCreator$$Lambda$4 implements OnClickListener {
    private final Runnable arg$1;

    AlertsCreator$$Lambda$4(Runnable runnable) {
        this.arg$1 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$showSecretLocationAlert$4$AlertsCreator(this.arg$1, dialogInterface, i);
    }
}
