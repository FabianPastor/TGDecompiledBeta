package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$ggiuvz6vZ37WaSls81dRSwQOWdc implements RequestDelegate {
    private final /* synthetic */ ArticleViewer f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$ArticleViewer$ggiuvz6vZ37WaSls81dRSwQOWdc(ArticleViewer articleViewer, int i, long j) {
        this.f$0 = articleViewer;
        this.f$1 = i;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$10$ArticleViewer(this.f$1, this.f$2, tLObject, tL_error);
    }
}
