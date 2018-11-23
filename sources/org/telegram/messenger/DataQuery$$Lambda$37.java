package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$37 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;
    private final boolean arg$3;
    private final int arg$4;
    private final int arg$5;
    private final int arg$6;
    private final int arg$7;

    DataQuery$$Lambda$37(DataQuery dataQuery, long j, boolean z, int i, int i2, int i3, int i4) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = z;
        this.arg$4 = i;
        this.arg$5 = i2;
        this.arg$6 = i3;
        this.arg$7 = i4;
    }

    public void run() {
        this.arg$1.lambda$processLoadedMediaCount$60$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
