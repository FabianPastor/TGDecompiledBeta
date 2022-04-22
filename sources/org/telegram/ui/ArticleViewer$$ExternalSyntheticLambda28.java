package org.telegram.ui;

import java.util.ArrayList;
import java.util.HashMap;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda28 implements Runnable {
    public final /* synthetic */ ArticleViewer f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ HashMap f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda28(ArticleViewer articleViewer, ArrayList arrayList, HashMap hashMap, String str, int i) {
        this.f$0 = articleViewer;
        this.f$1 = arrayList;
        this.f$2 = hashMap;
        this.f$3 = str;
        this.f$4 = i;
    }

    public final void run() {
        this.f$0.lambda$processSearch$27(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
