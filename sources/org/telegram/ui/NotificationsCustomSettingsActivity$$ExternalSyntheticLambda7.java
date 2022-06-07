package org.telegram.ui;

import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.MessagesStorage;
import org.telegram.ui.Cells.NotificationsCheckCell;

public final /* synthetic */ class NotificationsCustomSettingsActivity$$ExternalSyntheticLambda7 implements MessagesStorage.IntCallback {
    public final /* synthetic */ NotificationsCustomSettingsActivity f$0;
    public final /* synthetic */ NotificationsCheckCell f$1;
    public final /* synthetic */ RecyclerView.ViewHolder f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ NotificationsCustomSettingsActivity$$ExternalSyntheticLambda7(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity, NotificationsCheckCell notificationsCheckCell, RecyclerView.ViewHolder viewHolder, int i) {
        this.f$0 = notificationsCustomSettingsActivity;
        this.f$1 = notificationsCheckCell;
        this.f$2 = viewHolder;
        this.f$3 = i;
    }

    public final void run(int i) {
        this.f$0.lambda$createView$3(this.f$1, this.f$2, this.f$3, i);
    }
}
