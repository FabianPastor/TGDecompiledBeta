package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$50 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;
    private final int arg$3;

    DataQuery$$Lambda$50(DataQuery dataQuery, long j, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$increasePeerRaiting$81$DataQuery(this.arg$2, this.arg$3);
    }
}