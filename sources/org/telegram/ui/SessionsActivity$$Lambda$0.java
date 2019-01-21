package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class SessionsActivity$$Lambda$0 implements OnItemClickListener {
    private final SessionsActivity arg$1;

    SessionsActivity$$Lambda$0(SessionsActivity sessionsActivity) {
        this.arg$1 = sessionsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$11$SessionsActivity(view, i);
    }
}
