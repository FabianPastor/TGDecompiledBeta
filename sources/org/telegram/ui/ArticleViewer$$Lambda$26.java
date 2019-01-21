package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ArticleViewer$$Lambda$26 implements RequestDelegate {
    private final ArticleViewer arg$1;
    private final WebpageAdapter arg$2;
    private final int arg$3;
    private final BlockChannelCell arg$4;

    ArticleViewer$$Lambda$26(ArticleViewer articleViewer, WebpageAdapter webpageAdapter, int i, BlockChannelCell blockChannelCell) {
        this.arg$1 = articleViewer;
        this.arg$2 = webpageAdapter;
        this.arg$3 = i;
        this.arg$4 = blockChannelCell;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadChannel$31$ArticleViewer(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
