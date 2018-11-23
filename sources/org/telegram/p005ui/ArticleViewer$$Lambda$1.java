package org.telegram.p005ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$1 */
final /* synthetic */ class ArticleViewer$$Lambda$1 implements OnTouchListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$1(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$showPopup$1$ArticleViewer(view, motionEvent);
    }
}
