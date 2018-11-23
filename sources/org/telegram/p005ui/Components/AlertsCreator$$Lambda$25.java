package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesController;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$25 */
final /* synthetic */ class AlertsCreator$$Lambda$25 implements OnClickListener {
    private final int[] arg$1;

    AlertsCreator$$Lambda$25(int[] iArr) {
        this.arg$1 = iArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalMainSettings().edit().putInt("keep_media", this.arg$1[0]).commit();
    }
}
