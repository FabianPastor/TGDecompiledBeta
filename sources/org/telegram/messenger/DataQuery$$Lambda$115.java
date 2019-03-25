package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$115 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final LongSparseArray arg$3;
    private final ArrayList arg$4;
    private final int arg$5;
    private final int arg$6;

    DataQuery$$Lambda$115(DataQuery dataQuery, ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, int i, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = longSparseArray;
        this.arg$4 = arrayList2;
        this.arg$5 = i;
        this.arg$6 = i2;
    }

    public void run() {
        this.arg$1.lambda$null$22$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
