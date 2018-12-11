package org.telegram.p005ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$18 */
final /* synthetic */ class ArticleViewer$$Lambda$18 implements AnimatorUpdateListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$18(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.arg$1.lambda$checkScrollAnimated$21$ArticleViewer(valueAnimator);
    }
}
