package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$39 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;
    private final int arg$3;
    private final int arg$4;

    DataQuery$$Lambda$39(DataQuery dataQuery, long j, int i, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = i2;
    }

    public void run() {
        this.arg$1.lambda$getMediaCountDatabase$62$DataQuery(this.arg$2, this.arg$3, this.arg$4);
    }
}
