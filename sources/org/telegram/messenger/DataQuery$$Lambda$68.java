package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$68 implements Runnable {
    private final DataQuery arg$1;
    private final int arg$2;
    private final int arg$3;

    DataQuery$$Lambda$68(DataQuery dataQuery, int i, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = i2;
    }

    public void run() {
        this.arg$1.lambda$loadBotInfo$108$DataQuery(this.arg$2, this.arg$3);
    }
}
