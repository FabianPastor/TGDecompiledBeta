package org.telegram.p005ui;

import android.animation.AnimatorSet;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$30 */
final /* synthetic */ class ArticleViewer$$Lambda$30 implements Runnable {
    private final ArticleViewer arg$1;
    private final AnimatorSet arg$2;

    ArticleViewer$$Lambda$30(ArticleViewer articleViewer, AnimatorSet animatorSet) {
        this.arg$1 = articleViewer;
        this.arg$2 = animatorSet;
    }

    public void run() {
        this.arg$1.lambda$openPhoto$38$ArticleViewer(this.arg$2);
    }
}
