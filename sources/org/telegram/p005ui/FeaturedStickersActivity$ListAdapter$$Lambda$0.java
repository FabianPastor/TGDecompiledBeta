package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.FeaturedStickersActivity.ListAdapter;

/* renamed from: org.telegram.ui.FeaturedStickersActivity$ListAdapter$$Lambda$0 */
final /* synthetic */ class FeaturedStickersActivity$ListAdapter$$Lambda$0 implements OnClickListener {
    private final ListAdapter arg$1;

    FeaturedStickersActivity$ListAdapter$$Lambda$0(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public void onClick(View view) {
        this.arg$1.lambda$onCreateViewHolder$0$FeaturedStickersActivity$ListAdapter(view);
    }
}
