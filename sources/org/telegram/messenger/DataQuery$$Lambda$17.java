package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$17 implements Runnable {
    private final DataQuery arg$1;
    private final boolean arg$2;
    private final ArrayList arg$3;
    private final int arg$4;
    private final int arg$5;
    private final ArrayList arg$6;

    DataQuery$$Lambda$17(DataQuery dataQuery, boolean z, ArrayList arrayList, int i, int i2, ArrayList arrayList2) {
        this.arg$1 = dataQuery;
        this.arg$2 = z;
        this.arg$3 = arrayList;
        this.arg$4 = i;
        this.arg$5 = i2;
        this.arg$6 = arrayList2;
    }

    public void run() {
        this.arg$1.lambda$processLoadedFeaturedStickers$24$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
