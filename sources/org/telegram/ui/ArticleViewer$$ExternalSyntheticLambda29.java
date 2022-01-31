package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$WebPage;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda29 implements Runnable {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC$WebPage f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ String f$5;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda29(ArticleViewer articleViewer, TLObject tLObject, TLRPC$WebPage tLRPC$WebPage, MessageObject messageObject, int i, String str) {
        this.f$0 = articleViewer;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$WebPage;
        this.f$3 = messageObject;
        this.f$4 = i;
        this.f$5 = str;
    }

    public final void run() {
        this.f$0.lambda$open$29(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
