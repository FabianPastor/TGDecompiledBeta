package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$11 implements Runnable {
    private final DataQuery arg$1;
    private final boolean arg$2;
    private final int arg$3;
    private final ArrayList arg$4;
    private final boolean arg$5;
    private final int arg$6;

    DataQuery$$Lambda$11(DataQuery dataQuery, boolean z, int i, ArrayList arrayList, boolean z2, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = z;
        this.arg$3 = i;
        this.arg$4 = arrayList;
        this.arg$5 = z2;
        this.arg$6 = i2;
    }

    public void run() {
        this.arg$1.lambda$processLoadedRecentDocuments$14$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
