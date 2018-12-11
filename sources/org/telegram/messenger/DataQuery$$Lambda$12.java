package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$12 implements Runnable {
    private final DataQuery arg$1;
    private final boolean arg$2;
    private final int arg$3;
    private final ArrayList arg$4;

    DataQuery$$Lambda$12(DataQuery dataQuery, boolean z, int i, ArrayList arrayList) {
        this.arg$1 = dataQuery;
        this.arg$2 = z;
        this.arg$3 = i;
        this.arg$4 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$processLoadedRecentDocuments$15$DataQuery(this.arg$2, this.arg$3, this.arg$4);
    }
}
