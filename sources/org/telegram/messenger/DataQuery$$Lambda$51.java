package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$51 implements Runnable {
    private final DataQuery arg$1;
    private final int arg$2;
    private final int arg$3;
    private final double arg$4;

    DataQuery$$Lambda$51(DataQuery dataQuery, int i, int i2, double d) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = d;
    }

    public void run() {
        this.arg$1.lambda$savePeer$82$DataQuery(this.arg$2, this.arg$3, this.arg$4);
    }
}
