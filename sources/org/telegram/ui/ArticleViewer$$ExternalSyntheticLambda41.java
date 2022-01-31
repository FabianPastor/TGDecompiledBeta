package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda41 implements RecyclerListView.OnItemLongClickListener {
    public final /* synthetic */ ArticleViewer f$0;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda41(ArticleViewer articleViewer) {
        this.f$0 = articleViewer;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.lambda$setParentActivity$8(view, i);
    }
}
