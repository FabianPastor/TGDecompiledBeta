package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda32 implements RequestDelegate {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda32(ArticleViewer articleViewer, int i, long j) {
        this.f$0 = articleViewer;
        this.f$1 = i;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$setParentActivity$10(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
