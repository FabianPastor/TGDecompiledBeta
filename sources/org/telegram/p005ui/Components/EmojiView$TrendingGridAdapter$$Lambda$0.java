package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.Components.EmojiView.TrendingGridAdapter;

/* renamed from: org.telegram.ui.Components.EmojiView$TrendingGridAdapter$$Lambda$0 */
final /* synthetic */ class EmojiView$TrendingGridAdapter$$Lambda$0 implements OnClickListener {
    private final TrendingGridAdapter arg$1;

    EmojiView$TrendingGridAdapter$$Lambda$0(TrendingGridAdapter trendingGridAdapter) {
        this.arg$1 = trendingGridAdapter;
    }

    public void onClick(View view) {
        this.arg$1.lambda$onCreateViewHolder$0$EmojiView$TrendingGridAdapter(view);
    }
}
