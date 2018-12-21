package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.PollCreateActivity$$Lambda$0 */
final /* synthetic */ class PollCreateActivity$$Lambda$0 implements OnItemClickListener {
    private final PollCreateActivity arg$1;

    PollCreateActivity$$Lambda$0(PollCreateActivity pollCreateActivity) {
        this.arg$1 = pollCreateActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$PollCreateActivity(view, i);
    }
}
