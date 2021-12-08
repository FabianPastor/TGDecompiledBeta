package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda24 implements Runnable {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.WebPage f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ String f$5;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda24(ArticleViewer articleViewer, TLObject tLObject, TLRPC.WebPage webPage, MessageObject messageObject, int i, String str) {
        this.f$0 = articleViewer;
        this.f$1 = tLObject;
        this.f$2 = webPage;
        this.f$3 = messageObject;
        this.f$4 = i;
        this.f$5 = str;
    }

    public final void run() {
        this.f$0.m1419lambda$open$28$orgtelegramuiArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
