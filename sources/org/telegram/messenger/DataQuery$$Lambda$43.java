package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$43 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;

    DataQuery$$Lambda$43(DataQuery dataQuery, ArrayList arrayList) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$buildShortcuts$67$DataQuery(this.arg$2);
    }
}
