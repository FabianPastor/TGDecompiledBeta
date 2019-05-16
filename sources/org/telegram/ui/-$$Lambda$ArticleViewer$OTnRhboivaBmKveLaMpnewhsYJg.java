package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$OTnRhboivaBmKveLaMpnewhsYJg implements RequestDelegate {
    private final /* synthetic */ ArticleViewer f$0;
    private final /* synthetic */ WebpageAdapter f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ BlockChannelCell f$3;

    public /* synthetic */ -$$Lambda$ArticleViewer$OTnRhboivaBmKveLaMpnewhsYJg(ArticleViewer articleViewer, WebpageAdapter webpageAdapter, int i, BlockChannelCell blockChannelCell) {
        this.f$0 = articleViewer;
        this.f$1 = webpageAdapter;
        this.f$2 = i;
        this.f$3 = blockChannelCell;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadChannel$31$ArticleViewer(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
