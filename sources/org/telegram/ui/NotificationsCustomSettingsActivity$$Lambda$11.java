package org.telegram.ui;

import org.telegram.ui.NotificationsSettingsActivity.NotificationException;
import org.telegram.ui.ProfileNotificationsActivity.ProfileNotificationsActivityDelegate;

final /* synthetic */ class NotificationsCustomSettingsActivity$$Lambda$11 implements ProfileNotificationsActivityDelegate {
    private final NotificationsCustomSettingsActivity arg$1;

    NotificationsCustomSettingsActivity$$Lambda$11(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity) {
        this.arg$1 = notificationsCustomSettingsActivity;
    }

    public void didCreateNewException(NotificationException notificationException) {
        this.arg$1.lambda$null$1$NotificationsCustomSettingsActivity(notificationException);
    }
}
