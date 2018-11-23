package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class DataQuery$$Lambda$41 implements Runnable {
    private final DataQuery arg$1;
    private final ArrayList arg$2;
    private final boolean arg$3;
    private final long arg$4;
    private final int arg$5;
    private final int arg$6;

    DataQuery$$Lambda$41(DataQuery dataQuery, ArrayList arrayList, boolean z, long j, int i, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = arrayList;
        this.arg$3 = z;
        this.arg$4 = j;
        this.arg$5 = i;
        this.arg$6 = i2;
    }

    public void run() {
        this.arg$1.lambda$putMediaDatabase$64$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
