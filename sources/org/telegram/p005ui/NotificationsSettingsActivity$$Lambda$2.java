package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;

/* renamed from: org.telegram.ui.NotificationsSettingsActivity$$Lambda$2 */
final /* synthetic */ class NotificationsSettingsActivity$$Lambda$2 implements OnClickListener {
    private final NotificationsSettingsActivity arg$1;
    private final ArrayList arg$2;

    NotificationsSettingsActivity$$Lambda$2(NotificationsSettingsActivity notificationsSettingsActivity, ArrayList arrayList) {
        this.arg$1 = notificationsSettingsActivity;
        this.arg$2 = arrayList;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showExceptionsAlert$9$NotificationsSettingsActivity(this.arg$2, dialogInterface, i);
    }
}
