package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$WebPage;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda35 implements RequestDelegate {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ TLRPC$WebPage f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda35(ArticleViewer articleViewer, TLRPC$WebPage tLRPC$WebPage, MessageObject messageObject, int i, String str) {
        this.f$0 = articleViewer;
        this.f$1 = tLRPC$WebPage;
        this.f$2 = messageObject;
        this.f$3 = i;
        this.f$4 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$open$31(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
