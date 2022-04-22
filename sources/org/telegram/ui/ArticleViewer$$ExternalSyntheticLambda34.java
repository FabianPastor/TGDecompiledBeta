package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getWebPage;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda34 implements RequestDelegate {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC$TL_messages_getWebPage f$3;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda34(ArticleViewer articleViewer, int i, String str, TLRPC$TL_messages_getWebPage tLRPC$TL_messages_getWebPage) {
        this.f$0 = articleViewer;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = tLRPC$TL_messages_getWebPage;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$openWebpageUrl$7(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
