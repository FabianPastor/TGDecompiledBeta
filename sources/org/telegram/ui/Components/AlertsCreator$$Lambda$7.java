package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class AlertsCreator$$Lambda$7 implements OnClickListener {
    private final Runnable arg$1;

    AlertsCreator$$Lambda$7(Runnable runnable) {
        this.arg$1 = runnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createClearOrDeleteDialogAlert$9$AlertsCreator(this.arg$1, dialogInterface, i);
    }
}
