package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$119 implements Runnable {
    private final DataQuery arg$1;
    private final boolean arg$2;
    private final ArrayList arg$3;
    private final int arg$4;

    DataQuery$$Lambda$119(DataQuery dataQuery, boolean z, ArrayList arrayList, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = z;
        this.arg$3 = arrayList;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$null$10$DataQuery(this.arg$2, this.arg$3, this.arg$4);
    }
}
