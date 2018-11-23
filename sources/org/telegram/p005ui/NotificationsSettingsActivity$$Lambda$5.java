package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.NotificationsSettingsActivity$$Lambda$5 */
final /* synthetic */ class NotificationsSettingsActivity$$Lambda$5 implements OnClickListener {
    private final NotificationsSettingsActivity arg$1;
    private final int arg$2;

    NotificationsSettingsActivity$$Lambda$5(NotificationsSettingsActivity notificationsSettingsActivity, int i) {
        this.arg$1 = notificationsSettingsActivity;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$6$NotificationsSettingsActivity(this.arg$2, dialogInterface, i);
    }
}
