package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class StickersActivity$$Lambda$0 implements OnItemClickListener {
    private final StickersActivity arg$1;

    StickersActivity$$Lambda$0(StickersActivity stickersActivity) {
        this.arg$1 = stickersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$StickersActivity(view, i);
    }
}
