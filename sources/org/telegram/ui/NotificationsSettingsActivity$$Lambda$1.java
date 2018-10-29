package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class NotificationsSettingsActivity$$Lambda$1 implements OnItemClickListener {
    private final NotificationsSettingsActivity arg$1;

    NotificationsSettingsActivity$$Lambda$1(NotificationsSettingsActivity notificationsSettingsActivity) {
        this.arg$1 = notificationsSettingsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$10$NotificationsSettingsActivity(view, i);
    }
}
