package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$gN3ysqedASIoOc6B-30_HuSHg6g implements OnItemLongClickListener {
    private final /* synthetic */ ArticleViewer f$0;

    public /* synthetic */ -$$Lambda$ArticleViewer$gN3ysqedASIoOc6B-30_HuSHg6g(ArticleViewer articleViewer) {
        this.f$0 = articleViewer;
    }

    public final boolean onItemClick(View view, int i) {
        return this.f$0.lambda$setParentActivity$8$ArticleViewer(view, i);
    }
}
