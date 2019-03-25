package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesStorage.IntCallback;

final /* synthetic */ class AlertsCreator$$Lambda$25 implements OnClickListener {
    private final int[] arg$1;
    private final IntCallback arg$2;

    AlertsCreator$$Lambda$25(int[] iArr, IntCallback intCallback) {
        this.arg$1 = iArr;
        this.arg$2 = intCallback;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createLocationUpdateDialog$28$AlertsCreator(this.arg$1, this.arg$2, dialogInterface, i);
    }
}
