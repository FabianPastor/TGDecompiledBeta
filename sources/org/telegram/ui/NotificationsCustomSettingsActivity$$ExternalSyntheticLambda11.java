package org.telegram.ui;

import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;

public final /* synthetic */ class NotificationsCustomSettingsActivity$$ExternalSyntheticLambda11 implements ProfileNotificationsActivity.ProfileNotificationsActivityDelegate {
    public final /* synthetic */ NotificationsCustomSettingsActivity f$0;

    public /* synthetic */ NotificationsCustomSettingsActivity$$ExternalSyntheticLambda11(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity) {
        this.f$0 = notificationsCustomSettingsActivity;
    }

    public final void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException) {
        this.f$0.lambda$createView$0(notificationException);
    }

    public /* synthetic */ void didRemoveException(long j) {
        ProfileNotificationsActivity.ProfileNotificationsActivityDelegate.CC.$default$didRemoveException(this, j);
    }
}
