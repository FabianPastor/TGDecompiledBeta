package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$61 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final boolean arg$3;
    private final ArrayList arg$4;
    private final ArrayList arg$5;
    private final SparseArray arg$6;
    private final SparseArray arg$7;
    private final SparseArray arg$8;
    private final long arg$9;

    DataQuery$$Lambda$61(DataQuery dataQuery, ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray, SparseArray sparseArray2, SparseArray sparseArray3, long j) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = z;
        this.arg$4 = arrayList2;
        this.arg$5 = arrayList3;
        this.arg$6 = sparseArray;
        this.arg$7 = sparseArray2;
        this.arg$8 = sparseArray3;
        this.arg$9 = j;
    }

    public void run() {
        this.arg$1.lambda$broadcastReplyMessages$96$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
