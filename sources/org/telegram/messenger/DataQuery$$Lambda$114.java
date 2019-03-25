package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$114 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final int arg$3;

    DataQuery$$Lambda$114(DataQuery dataQuery, ArrayList arrayList, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$null$21$DataQuery(this.arg$2, this.arg$3);
    }
}
