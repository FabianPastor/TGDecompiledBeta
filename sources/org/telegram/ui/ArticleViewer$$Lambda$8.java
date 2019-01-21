package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class ArticleViewer$$Lambda$8 implements OnItemClickListener {
    private final ArticleViewer arg$1;
    private final WebpageAdapter arg$2;

    ArticleViewer$$Lambda$8(ArticleViewer articleViewer, WebpageAdapter webpageAdapter) {
        this.arg$1 = articleViewer;
        this.arg$2 = webpageAdapter;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$setParentActivity$11$ArticleViewer(this.arg$2, view, i);
    }
}
