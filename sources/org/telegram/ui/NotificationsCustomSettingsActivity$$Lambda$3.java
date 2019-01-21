package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.Cells.NotificationsCheckCell;

final /* synthetic */ class NotificationsCustomSettingsActivity$$Lambda$3 implements IntCallback {
    private final NotificationsCustomSettingsActivity arg$1;
    private final NotificationsCheckCell arg$2;
    private final ViewHolder arg$3;
    private final int arg$4;

    NotificationsCustomSettingsActivity$$Lambda$3(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity, NotificationsCheckCell notificationsCheckCell, ViewHolder viewHolder, int i) {
        this.arg$1 = notificationsCustomSettingsActivity;
        this.arg$2 = notificationsCheckCell;
        this.arg$3 = viewHolder;
        this.arg$4 = i;
    }

    public void run(int i) {
        this.arg$1.lambda$null$1$NotificationsCustomSettingsActivity(this.arg$2, this.arg$3, this.arg$4, i);
    }
}
