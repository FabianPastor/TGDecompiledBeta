package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class NotificationsSettingsActivity$$Lambda$8 implements OnClickListener {
    private final NotificationsSettingsActivity arg$1;
    private final int arg$2;

    NotificationsSettingsActivity$$Lambda$8(NotificationsSettingsActivity notificationsSettingsActivity, int i) {
        this.arg$1 = notificationsSettingsActivity;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$9$NotificationsSettingsActivity(this.arg$2, dialogInterface, i);
    }
}
