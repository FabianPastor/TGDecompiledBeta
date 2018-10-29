package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesStorage.IntCallback;

final /* synthetic */ class AlertsCreator$$Lambda$21 implements OnClickListener {
    private final IntCallback arg$1;

    AlertsCreator$$Lambda$21(IntCallback intCallback) {
        this.arg$1 = intCallback;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.run(0);
    }
}
