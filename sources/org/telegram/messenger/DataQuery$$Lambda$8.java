package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$8 implements Runnable {
    private final DataQuery arg$1;
    private final boolean arg$2;
    private final int arg$3;

    DataQuery$$Lambda$8(DataQuery dataQuery, boolean z, int i) {
        this.arg$1 = dataQuery;
        this.arg$2 = z;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$loadRecents$11$DataQuery(this.arg$2, this.arg$3);
    }
}
