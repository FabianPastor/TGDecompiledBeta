package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$102 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;
    private final int[] arg$3;

    DataQuery$$Lambda$102(DataQuery dataQuery, long j, int[] iArr) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = iArr;
    }

    public void run() {
        this.arg$1.lambda$null$55$DataQuery(this.arg$2, this.arg$3);
    }
}
