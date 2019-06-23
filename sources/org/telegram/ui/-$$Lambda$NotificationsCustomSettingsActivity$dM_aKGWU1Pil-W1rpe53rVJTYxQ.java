package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.ui.NotificationsSettingsActivity.NotificationException;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsCustomSettingsActivity$dM_aKGWU1Pil-W1rpe53rVJTYxQ implements IntCallback {
    private final /* synthetic */ NotificationsCustomSettingsActivity f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ NotificationException f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$NotificationsCustomSettingsActivity$dM_aKGWU1Pil-W1rpe53rVJTYxQ(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity, boolean z, ArrayList arrayList, NotificationException notificationException, int i) {
        this.f$0 = notificationsCustomSettingsActivity;
        this.f$1 = z;
        this.f$2 = arrayList;
        this.f$3 = notificationException;
        this.f$4 = i;
    }

    public final void run(int i) {
        this.f$0.lambda$null$0$NotificationsCustomSettingsActivity(this.f$1, this.f$2, this.f$3, this.f$4, i);
    }
}
