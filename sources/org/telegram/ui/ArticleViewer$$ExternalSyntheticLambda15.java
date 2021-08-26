package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda15 implements TextView.OnEditorActionListener {
    public final /* synthetic */ ArticleViewer f$0;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda15(ArticleViewer articleViewer) {
        this.f$0 = articleViewer;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$setParentActivity$14(textView, i, keyEvent);
    }
}
