package org.telegram.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

final /* synthetic */ class ArticleViewer$$Lambda$18 implements AnimatorUpdateListener {
    private final ArticleViewer arg$1;

    ArticleViewer$$Lambda$18(ArticleViewer articleViewer) {
        this.arg$1 = articleViewer;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.arg$1.lambda$checkScrollAnimated$21$ArticleViewer(valueAnimator);
    }
}
