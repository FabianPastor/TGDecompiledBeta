package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ArticleViewer;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda31 implements RequestDelegate {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ ArticleViewer.BlockChannelCell f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.TL_channels_joinChannel f$3;
    public final /* synthetic */ TLRPC.Chat f$4;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda31(ArticleViewer articleViewer, ArticleViewer.BlockChannelCell blockChannelCell, int i, TLRPC.TL_channels_joinChannel tL_channels_joinChannel, TLRPC.Chat chat) {
        this.f$0 = articleViewer;
        this.f$1 = blockChannelCell;
        this.f$2 = i;
        this.f$3 = tL_channels_joinChannel;
        this.f$4 = chat;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2669lambda$joinChannel$43$orgtelegramuiArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
