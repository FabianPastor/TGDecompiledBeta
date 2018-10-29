package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.ui.NotificationsSettingsActivity.NotificationException;

final /* synthetic */ class NotificationsExceptionsActivity$$Lambda$2 implements IntCallback {
    private final NotificationsExceptionsActivity arg$1;
    private final ArrayList arg$2;
    private final NotificationException arg$3;
    private final int arg$4;

    NotificationsExceptionsActivity$$Lambda$2(NotificationsExceptionsActivity notificationsExceptionsActivity, ArrayList arrayList, NotificationException notificationException, int i) {
        this.arg$1 = notificationsExceptionsActivity;
        this.arg$2 = arrayList;
        this.arg$3 = notificationException;
        this.arg$4 = i;
    }

    public void run(int i) {
        this.arg$1.lambda$null$0$NotificationsExceptionsActivity(this.arg$2, this.arg$3, this.arg$4, i);
    }
}
