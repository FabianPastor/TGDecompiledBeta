package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$86 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;
    private final ArrayList arg$3;

    DataQuery$$Lambda$86(DataQuery dataQuery, long j, ArrayList arrayList) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$65$DataQuery(this.arg$2, this.arg$3);
    }
}
