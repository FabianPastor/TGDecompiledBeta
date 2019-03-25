package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$111 implements Runnable {
    private final DataQuery arg$1;
    private final int arg$2;
    private final int arg$3;

    DataQuery$$Lambda$111(DataQuery dataQuery, int i, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = i2;
    }

    public void run() {
        this.arg$1.lambda$null$40$DataQuery(this.arg$2, this.arg$3);
    }
}
