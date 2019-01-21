package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class FeaturedStickersActivity$$Lambda$0 implements OnItemClickListener {
    private final FeaturedStickersActivity arg$1;

    FeaturedStickersActivity$$Lambda$0(FeaturedStickersActivity featuredStickersActivity) {
        this.arg$1 = featuredStickersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$FeaturedStickersActivity(view, i);
    }
}
