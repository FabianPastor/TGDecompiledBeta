package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.p005ui.NotificationsSettingsActivity.NotificationException;

/* renamed from: org.telegram.ui.NotificationsCustomSettingsActivity$$Lambda$2 */
final /* synthetic */ class NotificationsCustomSettingsActivity$$Lambda$2 implements IntCallback {
    private final NotificationsCustomSettingsActivity arg$1;
    private final ArrayList arg$2;
    private final NotificationException arg$3;
    private final int arg$4;

    NotificationsCustomSettingsActivity$$Lambda$2(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity, ArrayList arrayList, NotificationException notificationException, int i) {
        this.arg$1 = notificationsCustomSettingsActivity;
        this.arg$2 = arrayList;
        this.arg$3 = notificationException;
        this.arg$4 = i;
    }

    public void run(int i) {
        this.arg$1.lambda$null$0$NotificationsCustomSettingsActivity(this.arg$2, this.arg$3, this.arg$4, i);
    }
}
