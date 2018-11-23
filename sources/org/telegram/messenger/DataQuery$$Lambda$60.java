package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$60 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final SparseArray arg$3;

    DataQuery$$Lambda$60(DataQuery dataQuery, ArrayList arrayList, SparseArray sparseArray) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = sparseArray;
    }

    public void run() {
        this.arg$1.lambda$saveReplyMessages$95$DataQuery(this.arg$2, this.arg$3);
    }
}
