package org.telegram.p005ui;

import org.telegram.p005ui.ArticleViewer.BlockChannelCell;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$39 */
final /* synthetic */ class ArticleViewer$$Lambda$39 implements Runnable {
    private final ArticleViewer arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final int arg$4;
    private final BlockChannelCell arg$5;

    ArticleViewer$$Lambda$39(ArticleViewer articleViewer, TL_error tL_error, TLObject tLObject, int i, BlockChannelCell blockChannelCell) {
        this.arg$1 = articleViewer;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = i;
        this.arg$5 = blockChannelCell;
    }

    public void run() {
        this.arg$1.lambda$null$30$ArticleViewer(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
