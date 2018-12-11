package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$27 implements Runnable {
    private final DataQuery arg$1;
    private final int arg$2;

    DataQuery$$Lambda$27(DataQuery dataQuery, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
    }

    public void run() {
        this.arg$1.lambda$processLoadedStickers$37$DataQuery(this.arg$2);
    }
}
