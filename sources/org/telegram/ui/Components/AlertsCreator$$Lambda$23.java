package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesController;

final /* synthetic */ class AlertsCreator$$Lambda$23 implements OnClickListener {
    private final int[] arg$1;

    AlertsCreator$$Lambda$23(int[] iArr) {
        this.arg$1 = iArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalMainSettings().edit().putInt("keep_media", this.arg$1[0]).commit();
    }
}
