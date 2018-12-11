package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$42 */
final /* synthetic */ class ArticleViewer$$Lambda$42 implements Runnable {
    private final ArticleViewer arg$1;
    private final TLObject arg$2;
    private final int arg$3;
    private final long arg$4;

    ArticleViewer$$Lambda$42(ArticleViewer articleViewer, TLObject tLObject, int i, long j) {
        this.arg$1 = articleViewer;
        this.arg$2 = tLObject;
        this.arg$3 = i;
        this.arg$4 = j;
    }

    public void run() {
        this.arg$1.lambda$null$9$ArticleViewer(this.arg$2, this.arg$3, this.arg$4);
    }
}
