package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;
import java.util.HashMap;

final /* synthetic */ class DataQuery$$Lambda$109 implements Runnable {
    private final DataQuery arg$1;
    private final int arg$2;
    private final LongSparseArray arg$3;
    private final HashMap arg$4;
    private final ArrayList arg$5;
    private final int arg$6;
    private final int arg$7;
    private final HashMap arg$8;
    private final LongSparseArray arg$9;

    DataQuery$$Lambda$109(DataQuery dataQuery, int i, LongSparseArray longSparseArray, HashMap hashMap, ArrayList arrayList, int i2, int i3, HashMap hashMap2, LongSparseArray longSparseArray2) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = longSparseArray;
        this.arg$4 = hashMap;
        this.arg$5 = arrayList;
        this.arg$6 = i2;
        this.arg$7 = i3;
        this.arg$8 = hashMap2;
        this.arg$9 = longSparseArray2;
    }

    public void run() {
        this.arg$1.lambda$null$39$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
