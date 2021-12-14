package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda39 implements RecyclerListView.OnItemClickListenerExtended {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ ArticleViewer.WebpageAdapter f$1;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda39(ArticleViewer articleViewer, ArticleViewer.WebpageAdapter webpageAdapter) {
        this.f$0 = articleViewer;
        this.f$1 = webpageAdapter;
    }

    public final void onItemClick(View view, int i, float f, float f2) {
        this.f$0.lambda$setParentActivity$11(this.f$1, view, i, f, f2);
    }
}
