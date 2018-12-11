package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$7 */
final /* synthetic */ class ArticleViewer$$Lambda$7 implements OnItemLongClickListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$7(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$setParentActivity$8$ArticleViewer(view, i);
    }
}
