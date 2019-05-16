package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.WebPage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$l3_iiPHXy4_uEQn5oag7li6c_dQ implements Runnable {
    private final /* synthetic */ ArticleViewer f$0;
    private final /* synthetic */ WebPage f$1;
    private final /* synthetic */ TL_webPage f$2;
    private final /* synthetic */ MessageObject f$3;
    private final /* synthetic */ String f$4;

    public /* synthetic */ -$$Lambda$ArticleViewer$l3_iiPHXy4_uEQn5oag7li6c_dQ(ArticleViewer articleViewer, WebPage webPage, TL_webPage tL_webPage, MessageObject messageObject, String str) {
        this.f$0 = articleViewer;
        this.f$1 = webPage;
        this.f$2 = tL_webPage;
        this.f$3 = messageObject;
        this.f$4 = str;
    }

    public final void run() {
        this.f$0.lambda$null$22$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
