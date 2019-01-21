package org.telegram.ui.Adapters;

import java.util.ArrayList;

final /* synthetic */ class SearchAdapter$$Lambda$2 implements Runnable {
    private final SearchAdapter arg$1;
    private final String arg$2;
    private final ArrayList arg$3;
    private final int arg$4;

    SearchAdapter$$Lambda$2(SearchAdapter searchAdapter, String str, ArrayList arrayList, int i) {
        this.arg$1 = searchAdapter;
        this.arg$2 = str;
        this.arg$3 = arrayList;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$null$0$SearchAdapter(this.arg$2, this.arg$3, this.arg$4);
    }
}
