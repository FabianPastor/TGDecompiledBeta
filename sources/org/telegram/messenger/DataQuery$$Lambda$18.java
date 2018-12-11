package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$18 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final ArrayList arg$3;
    private final int arg$4;
    private final int arg$5;

    DataQuery$$Lambda$18(DataQuery dataQuery, ArrayList arrayList, ArrayList arrayList2, int i, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = arrayList2;
        this.arg$4 = i;
        this.arg$5 = i2;
    }

    public void run() {
        this.arg$1.lambda$putFeaturedStickersToCache$25$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
