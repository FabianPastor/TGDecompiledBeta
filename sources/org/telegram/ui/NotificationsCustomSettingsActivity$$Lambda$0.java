package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended;

final /* synthetic */ class NotificationsCustomSettingsActivity$$Lambda$0 implements OnItemClickListenerExtended {
    private final NotificationsCustomSettingsActivity arg$1;

    NotificationsCustomSettingsActivity$$Lambda$0(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity) {
        this.arg$1 = notificationsCustomSettingsActivity;
    }

    public void onItemClick(View view, int i, float f, float f2) {
        this.arg$1.lambda$createView$6$NotificationsCustomSettingsActivity(view, i, f, f2);
    }
}
