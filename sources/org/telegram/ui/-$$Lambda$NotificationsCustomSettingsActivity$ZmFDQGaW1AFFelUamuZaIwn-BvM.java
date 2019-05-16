package org.telegram.ui;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.ui.Cells.NotificationsCheckCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$NotificationsCustomSettingsActivity$ZmFDQGaW1AFFelUamuZaIwn-BvM implements IntCallback {
    private final /* synthetic */ NotificationsCustomSettingsActivity f$0;
    private final /* synthetic */ NotificationsCheckCell f$1;
    private final /* synthetic */ ViewHolder f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$NotificationsCustomSettingsActivity$ZmFDQGaW1AFFelUamuZaIwn-BvM(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity, NotificationsCheckCell notificationsCheckCell, ViewHolder viewHolder, int i) {
        this.f$0 = notificationsCustomSettingsActivity;
        this.f$1 = notificationsCheckCell;
        this.f$2 = viewHolder;
        this.f$3 = i;
    }

    public final void run(int i) {
        this.f$0.lambda$null$3$NotificationsCustomSettingsActivity(this.f$1, this.f$2, this.f$3, i);
    }
}
