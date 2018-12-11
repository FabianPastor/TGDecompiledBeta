package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.SessionsActivity$$Lambda$0 */
final /* synthetic */ class SessionsActivity$$Lambda$0 implements OnItemClickListener {
    private final SessionsActivity arg$1;

    SessionsActivity$$Lambda$0(SessionsActivity sessionsActivity) {
        this.arg$1 = sessionsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$11$SessionsActivity(view, i);
    }
}
