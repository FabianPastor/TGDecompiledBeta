package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage;
import org.telegram.ui.NotificationsSettingsActivity;

public final /* synthetic */ class NotificationsCustomSettingsActivity$$ExternalSyntheticLambda11 implements MessagesStorage.IntCallback {
    public final /* synthetic */ NotificationsCustomSettingsActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ NotificationsSettingsActivity.NotificationException f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ NotificationsCustomSettingsActivity$$ExternalSyntheticLambda11(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity, boolean z, ArrayList arrayList, NotificationsSettingsActivity.NotificationException notificationException, int i) {
        this.f$0 = notificationsCustomSettingsActivity;
        this.f$1 = z;
        this.f$2 = arrayList;
        this.f$3 = notificationException;
        this.f$4 = i;
    }

    public final void run(int i) {
        this.f$0.m2645x27d99f9b(this.f$1, this.f$2, this.f$3, this.f$4, i);
    }
}
