package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$58 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final long arg$3;
    private final LongSparseArray arg$4;

    DataQuery$$Lambda$58(DataQuery dataQuery, ArrayList arrayList, long j, LongSparseArray longSparseArray) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = j;
        this.arg$4 = longSparseArray;
    }

    public void run() {
        this.arg$1.lambda$loadReplyMessagesForMessages$91$DataQuery(this.arg$2, this.arg$3, this.arg$4);
    }
}
