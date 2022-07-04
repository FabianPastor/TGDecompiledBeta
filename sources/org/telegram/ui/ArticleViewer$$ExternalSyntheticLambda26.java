package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ArticleViewer;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ ArticleViewer.BlockChannelCell f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;
    public final /* synthetic */ TLRPC.TL_channels_joinChannel f$4;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda26(ArticleViewer articleViewer, ArticleViewer.BlockChannelCell blockChannelCell, int i, TLRPC.TL_error tL_error, TLRPC.TL_channels_joinChannel tL_channels_joinChannel) {
        this.f$0 = articleViewer;
        this.f$1 = blockChannelCell;
        this.f$2 = i;
        this.f$3 = tL_error;
        this.f$4 = tL_channels_joinChannel;
    }

    public final void run() {
        this.f$0.m2668lambda$joinChannel$40$orgtelegramuiArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
