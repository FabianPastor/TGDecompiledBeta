package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda19 implements Runnable {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC.TL_messages_getWebPage f$4;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda19(ArticleViewer articleViewer, int i, TLObject tLObject, String str, TLRPC.TL_messages_getWebPage tL_messages_getWebPage) {
        this.f$0 = articleViewer;
        this.f$1 = i;
        this.f$2 = tLObject;
        this.f$3 = str;
        this.f$4 = tL_messages_getWebPage;
    }

    public final void run() {
        this.f$0.m1383lambda$openWebpageUrl$6$orgtelegramuiArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
