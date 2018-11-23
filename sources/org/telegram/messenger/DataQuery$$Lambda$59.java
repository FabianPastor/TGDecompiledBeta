package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$59 implements Runnable {
    private final DataQuery arg$1;
    private final StringBuilder arg$2;
    private final long arg$3;
    private final ArrayList arg$4;
    private final SparseArray arg$5;
    private final int arg$6;

    DataQuery$$Lambda$59(DataQuery dataQuery, StringBuilder stringBuilder, long j, ArrayList arrayList, SparseArray sparseArray, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = stringBuilder;
        this.arg$3 = j;
        this.arg$4 = arrayList;
        this.arg$5 = sparseArray;
        this.arg$6 = i;
    }

    public void run() {
        this.arg$1.lambda$loadReplyMessagesForMessages$94$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
