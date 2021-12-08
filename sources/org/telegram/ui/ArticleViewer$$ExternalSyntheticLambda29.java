package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda29 implements RequestDelegate {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ TLRPC.WebPage f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda29(ArticleViewer articleViewer, TLRPC.WebPage webPage, MessageObject messageObject, int i, String str) {
        this.f$0 = articleViewer;
        this.f$1 = webPage;
        this.f$2 = messageObject;
        this.f$3 = i;
        this.f$4 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1420lambda$open$29$orgtelegramuiArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
