package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$8 */
final /* synthetic */ class ArticleViewer$$Lambda$8 implements OnItemClickListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$8(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$setParentActivity$11$ArticleViewer(view, i);
    }
}
