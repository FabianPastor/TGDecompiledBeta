package org.telegram.ui.Adapters;

import java.util.ArrayList;

public final /* synthetic */ class SearchAdapter$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SearchAdapter f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ SearchAdapter$$ExternalSyntheticLambda0(SearchAdapter searchAdapter, int i, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = searchAdapter;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$2(this.f$1, this.f$2, this.f$3);
    }
}
