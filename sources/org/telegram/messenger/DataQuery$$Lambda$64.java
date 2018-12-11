package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$64 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;
    private final int arg$3;
    private final long arg$4;

    DataQuery$$Lambda$64(DataQuery dataQuery, long j, int i, long j2) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = j2;
    }

    public void run() {
        this.arg$1.lambda$saveDraft$102$DataQuery(this.arg$2, this.arg$3, this.arg$4);
    }
}
