package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$6 */
final /* synthetic */ class ArticleViewer$$Lambda$6 implements OnApplyWindowInsetsListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$6(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return this.arg$1.lambda$setParentActivity$7$ArticleViewer(view, windowInsets);
    }
}
