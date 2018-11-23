package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$24 implements Runnable {
    private final DataQuery arg$1;
    private final int arg$2;

    DataQuery$$Lambda$24(DataQuery dataQuery, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$loadStickers$33$DataQuery(this.arg$2);
    }
}
