package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ArticleViewer$$Lambda$39 implements Runnable {
    private final ArticleViewer arg$1;
    private final WebpageAdapter arg$2;
    private final TL_error arg$3;
    private final TLObject arg$4;
    private final int arg$5;
    private final BlockChannelCell arg$6;

    ArticleViewer$$Lambda$39(ArticleViewer articleViewer, WebpageAdapter webpageAdapter, TL_error tL_error, TLObject tLObject, int i, BlockChannelCell blockChannelCell) {
        this.arg$1 = articleViewer;
        this.arg$2 = webpageAdapter;
        this.arg$3 = tL_error;
        this.arg$4 = tLObject;
        this.arg$5 = i;
        this.arg$6 = blockChannelCell;
    }

    public void run() {
        this.arg$1.lambda$null$30$ArticleViewer(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
