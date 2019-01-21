package org.telegram.ui.Adapters;

import java.util.ArrayList;

final /* synthetic */ class SearchAdapterHelper$$Lambda$3 implements Runnable {
    private final SearchAdapterHelper arg$1;
    private final ArrayList arg$2;

    SearchAdapterHelper$$Lambda$3(SearchAdapterHelper searchAdapterHelper, ArrayList arrayList) {
        this.arg$1 = searchAdapterHelper;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$putRecentHashtags$7$SearchAdapterHelper(this.arg$2);
    }
}
