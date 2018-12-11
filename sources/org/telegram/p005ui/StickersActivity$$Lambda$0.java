package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.StickersActivity$$Lambda$0 */
final /* synthetic */ class StickersActivity$$Lambda$0 implements OnItemClickListener {
    private final StickersActivity arg$1;

    StickersActivity$$Lambda$0(StickersActivity stickersActivity) {
        this.arg$1 = stickersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$StickersActivity(view, i);
    }
}
