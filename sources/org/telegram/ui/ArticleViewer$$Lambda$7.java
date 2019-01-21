package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

final /* synthetic */ class ArticleViewer$$Lambda$7 implements OnItemLongClickListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$7(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public boolean onItemClick(View view, int i) {
        return this.arg$1.lambda$setParentActivity$8$ArticleViewer(view, i);
    }
}
