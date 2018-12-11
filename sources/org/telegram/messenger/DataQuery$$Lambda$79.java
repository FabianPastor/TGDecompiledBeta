package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$79 implements Runnable {
    private final DataQuery arg$1;
    private final int arg$2;
    private final double arg$3;
    private final long arg$4;

    DataQuery$$Lambda$79(DataQuery dataQuery, int i, double d, long j) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = d;
        this.arg$4 = j;
    }

    public void run() {
        this.arg$1.lambda$null$80$DataQuery(this.arg$2, this.arg$3, this.arg$4);
    }
}
