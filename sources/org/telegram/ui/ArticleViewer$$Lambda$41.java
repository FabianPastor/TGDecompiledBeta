package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ArticleViewer$$Lambda$41 implements RequestDelegate {
    private final ArticleViewer arg$1;
    private final int arg$2;
    private final long arg$3;

    ArticleViewer$$Lambda$41(ArticleViewer articleViewer, int i, long j) {
        this.arg$1 = articleViewer;
        this.arg$2 = i;
        this.arg$3 = j;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$10$ArticleViewer(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
