package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$21 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;

    DataQuery$$Lambda$21(DataQuery dataQuery, long j) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
    }

    public void run() {
        this.arg$1.lambda$markFaturedStickersByIdAsRead$28$DataQuery(this.arg$2);
    }
}
