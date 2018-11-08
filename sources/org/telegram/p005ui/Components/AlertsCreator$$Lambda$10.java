package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$10 */
final /* synthetic */ class AlertsCreator$$Lambda$10 implements OnClickListener {
    private final long arg$1;

    AlertsCreator$$Lambda$10(long j) {
        this.arg$1 = j;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createMuteAlert$10$AlertsCreator(this.arg$1, dialogInterface, i);
    }
}
