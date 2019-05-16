package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$D7cgjgLrXEd0Ps-6XAby7DZGrbY implements OnItemClickListener {
    private final /* synthetic */ ArticleViewer f$0;
    private final /* synthetic */ WebpageAdapter f$1;

    public /* synthetic */ -$$Lambda$ArticleViewer$D7cgjgLrXEd0Ps-6XAby7DZGrbY(ArticleViewer articleViewer, WebpageAdapter webpageAdapter) {
        this.f$0 = articleViewer;
        this.f$1 = webpageAdapter;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$setParentActivity$11$ArticleViewer(this.f$1, view, i);
    }
}
