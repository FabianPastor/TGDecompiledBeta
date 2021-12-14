package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda28 implements Runnable {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda28(ArticleViewer articleViewer, TLObject tLObject, int i, long j) {
        this.f$0 = articleViewer;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$setParentActivity$9(this.f$1, this.f$2, this.f$3);
    }
}
