package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$42 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;
    private final long arg$3;

    DataQuery$$Lambda$42(DataQuery dataQuery, long j, long j2) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = j2;
    }

    public void run() {
        this.arg$1.lambda$loadMusic$66$DataQuery(this.arg$2, this.arg$3);
    }
}
