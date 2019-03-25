package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$108 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final int arg$3;
    private final int arg$4;

    DataQuery$$Lambda$108(DataQuery dataQuery, ArrayList arrayList, int i, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = i;
        this.arg$4 = i2;
    }

    public void run() {
        this.arg$1.lambda$null$38$DataQuery(this.arg$2, this.arg$3, this.arg$4);
    }
}
