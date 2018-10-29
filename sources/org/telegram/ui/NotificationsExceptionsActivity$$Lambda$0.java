package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class NotificationsExceptionsActivity$$Lambda$0 implements OnItemClickListener {
    private final NotificationsExceptionsActivity arg$1;

    NotificationsExceptionsActivity$$Lambda$0(NotificationsExceptionsActivity notificationsExceptionsActivity) {
        this.arg$1 = notificationsExceptionsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$NotificationsExceptionsActivity(view, i);
    }
}
