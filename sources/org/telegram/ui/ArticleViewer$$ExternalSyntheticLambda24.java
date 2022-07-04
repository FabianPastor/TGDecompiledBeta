package org.telegram.ui;

import java.util.ArrayList;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda24 implements Runnable {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda24(ArticleViewer articleViewer, int i, ArrayList arrayList, String str) {
        this.f$0 = articleViewer;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.lambda$processSearch$26(this.f$1, this.f$2, this.f$3);
    }
}
