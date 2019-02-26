package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class AlertsCreator$$Lambda$15 implements OnClickListener {
    private final long arg$1;

    AlertsCreator$$Lambda$15(long j) {
        this.arg$1 = j;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createMuteAlert$17$AlertsCreator(this.arg$1, dialogInterface, i);
    }
}
