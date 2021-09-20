package org.telegram.ui.Adapters;

import java.util.ArrayList;

public final /* synthetic */ class SearchAdapter$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ SearchAdapter f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ SearchAdapter$$ExternalSyntheticLambda2(SearchAdapter searchAdapter, String str, int i, ArrayList arrayList, int i2) {
        this.f$0 = searchAdapter;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = arrayList;
        this.f$4 = i2;
    }

    public final void run() {
        this.f$0.lambda$processSearch$0(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
