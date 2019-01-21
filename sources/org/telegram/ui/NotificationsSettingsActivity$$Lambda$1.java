package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended;

final /* synthetic */ class NotificationsSettingsActivity$$Lambda$1 implements OnItemClickListenerExtended {
    private final NotificationsSettingsActivity arg$1;

    NotificationsSettingsActivity$$Lambda$1(NotificationsSettingsActivity notificationsSettingsActivity) {
        this.arg$1 = notificationsSettingsActivity;
    }

    public void onItemClick(View view, int i, float f, float f2) {
        this.arg$1.lambda$createView$8$NotificationsSettingsActivity(view, i, f, f2);
    }
}
