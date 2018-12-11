package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$26 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final int arg$3;
    private final int arg$4;
    private final int arg$5;

    DataQuery$$Lambda$26(DataQuery dataQuery, ArrayList arrayList, int i, int i2, int i3) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = i;
        this.arg$4 = i2;
        this.arg$5 = i3;
    }

    public void run() {
        this.arg$1.lambda$putStickersToCache$36$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
