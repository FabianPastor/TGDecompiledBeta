package org.telegram.ui;

import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;

public final /* synthetic */ class NotificationsCustomSettingsActivity$$ExternalSyntheticLambda2 implements ProfileNotificationsActivity.ProfileNotificationsActivityDelegate {
    public final /* synthetic */ NotificationsCustomSettingsActivity f$0;

    public /* synthetic */ NotificationsCustomSettingsActivity$$ExternalSyntheticLambda2(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity) {
        this.f$0 = notificationsCustomSettingsActivity;
    }

    public final void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException) {
        this.f$0.m3972x27d99f9b(notificationException);
    }

    public /* synthetic */ void didRemoveException(long j) {
        ProfileNotificationsActivity.ProfileNotificationsActivityDelegate.CC.$default$didRemoveException(this, j);
    }
}
