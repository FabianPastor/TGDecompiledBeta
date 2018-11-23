package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListenerExtended;

/* renamed from: org.telegram.ui.NotificationsSettingsActivity$$Lambda$1 */
final /* synthetic */ class NotificationsSettingsActivity$$Lambda$1 implements OnItemClickListenerExtended {
    private final NotificationsSettingsActivity arg$1;

    NotificationsSettingsActivity$$Lambda$1(NotificationsSettingsActivity notificationsSettingsActivity) {
        this.arg$1 = notificationsSettingsActivity;
    }

    public void onItemClick(View view, int i, float f, float f2) {
        this.arg$1.lambda$createView$7$NotificationsSettingsActivity(view, i, f, f2);
    }
}
