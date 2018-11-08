package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesStorage.IntCallback;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$20 */
final /* synthetic */ class AlertsCreator$$Lambda$20 implements OnClickListener {
    private final IntCallback arg$1;

    AlertsCreator$$Lambda$20(IntCallback intCallback) {
        this.arg$1 = intCallback;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$79$MessagesStorage(1);
    }
}
