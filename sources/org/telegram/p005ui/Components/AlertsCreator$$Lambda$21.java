package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesStorage.IntCallback;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$21 */
final /* synthetic */ class AlertsCreator$$Lambda$21 implements OnClickListener {
    private final int[] arg$1;
    private final IntCallback arg$2;

    AlertsCreator$$Lambda$21(int[] iArr, IntCallback intCallback) {
        this.arg$1 = iArr;
        this.arg$2 = intCallback;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createLocationUpdateDialog$22$AlertsCreator(this.arg$1, this.arg$2, dialogInterface, i);
    }
}
