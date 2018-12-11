package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.PopupNotificationActivity$$Lambda$0 */
final /* synthetic */ class PopupNotificationActivity$$Lambda$0 implements OnClickListener {
    private final PopupNotificationActivity arg$1;

    PopupNotificationActivity$$Lambda$0(PopupNotificationActivity popupNotificationActivity) {
        this.arg$1 = popupNotificationActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onRequestPermissionsResult$0$PopupNotificationActivity(dialogInterface, i);
    }
}
