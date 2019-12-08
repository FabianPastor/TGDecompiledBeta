package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$8zB_D_KTS2A3fmuMNDyRkHeqC2U implements OnEditorActionListener {
    private final /* synthetic */ ArticleViewer f$0;

    public /* synthetic */ -$$Lambda$ArticleViewer$8zB_D_KTS2A3fmuMNDyRkHeqC2U(ArticleViewer articleViewer) {
        this.f$0 = articleViewer;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$setParentActivity$14$ArticleViewer(textView, i, keyEvent);
    }
}
