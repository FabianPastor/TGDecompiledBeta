package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ArticleViewer;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda37 implements RequestDelegate {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ ArticleViewer.WebpageAdapter f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ArticleViewer.BlockChannelCell f$3;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda37(ArticleViewer articleViewer, ArticleViewer.WebpageAdapter webpageAdapter, int i, ArticleViewer.BlockChannelCell blockChannelCell) {
        this.f$0 = articleViewer;
        this.f$1 = webpageAdapter;
        this.f$2 = i;
        this.f$3 = blockChannelCell;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadChannel$39(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
