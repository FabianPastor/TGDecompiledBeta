package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$wFBfqyMKleu-xH4t-92wfKx_WCw implements Runnable {
    private final /* synthetic */ ArticleViewer f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ TL_messages_getWebPage f$4;

    public /* synthetic */ -$$Lambda$ArticleViewer$wFBfqyMKleu-xH4t-92wfKx_WCw(ArticleViewer articleViewer, int i, TLObject tLObject, String str, TL_messages_getWebPage tL_messages_getWebPage) {
        this.f$0 = articleViewer;
        this.f$1 = i;
        this.f$2 = tLObject;
        this.f$3 = str;
        this.f$4 = tL_messages_getWebPage;
    }

    public final void run() {
        this.f$0.lambda$null$5$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
