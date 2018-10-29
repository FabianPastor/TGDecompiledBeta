package org.telegram.ui.Adapters;

import java.util.ArrayList;
import java.util.HashMap;

final /* synthetic */ class SearchAdapterHelper$$Lambda$7 implements Runnable {
    private final SearchAdapterHelper arg$1;
    private final ArrayList arg$2;
    private final HashMap arg$3;

    SearchAdapterHelper$$Lambda$7(SearchAdapterHelper searchAdapterHelper, ArrayList arrayList, HashMap hashMap) {
        this.arg$1 = searchAdapterHelper;
        this.arg$2 = arrayList;
        this.arg$3 = hashMap;
    }

    public void run() {
        this.arg$1.lambda$null$7$SearchAdapterHelper(this.arg$2, this.arg$3);
    }
}
