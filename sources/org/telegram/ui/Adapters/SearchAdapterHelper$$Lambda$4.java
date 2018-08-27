package org.telegram.ui.Adapters;

import java.util.ArrayList;

final /* synthetic */ class SearchAdapterHelper$$Lambda$4 implements Runnable {
    private final SearchAdapterHelper arg$1;
    private final ArrayList arg$2;

    SearchAdapterHelper$$Lambda$4(SearchAdapterHelper searchAdapterHelper, ArrayList arrayList) {
        this.arg$1 = searchAdapterHelper;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$putRecentHashtags$9$SearchAdapterHelper(this.arg$2);
    }
}
