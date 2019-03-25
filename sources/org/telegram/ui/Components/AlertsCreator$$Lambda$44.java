package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.tgnet.ConnectionsManager;

final /* synthetic */ class AlertsCreator$$Lambda$44 implements OnCancelListener {
    private final int arg$1;
    private final int arg$2;

    AlertsCreator$$Lambda$44(int i, int i2) {
        this.arg$1 = i;
        this.arg$2 = i2;
    }

    public void onCancel(DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.arg$1).cancelRequest(this.arg$2, true);
    }
}
