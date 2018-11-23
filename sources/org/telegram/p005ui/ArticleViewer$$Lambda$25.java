package org.telegram.p005ui;

import org.telegram.p005ui.ArticleViewer.BlockChannelCell;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$25 */
final /* synthetic */ class ArticleViewer$$Lambda$25 implements RequestDelegate {
    private final ArticleViewer arg$1;
    private final int arg$2;
    private final BlockChannelCell arg$3;

    ArticleViewer$$Lambda$25(ArticleViewer articleViewer, int i, BlockChannelCell blockChannelCell) {
        this.arg$1 = articleViewer;
        this.arg$2 = i;
        this.arg$3 = blockChannelCell;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadChannel$30$ArticleViewer(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
