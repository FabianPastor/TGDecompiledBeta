package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$40 implements Runnable {
    private final DataQuery arg$1;
    private final int arg$2;
    private final long arg$3;
    private final int arg$4;
    private final boolean arg$5;
    private final int arg$6;
    private final int arg$7;

    DataQuery$$Lambda$40(DataQuery dataQuery, int i, long j, int i2, boolean z, int i3, int i4) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = j;
        this.arg$4 = i2;
        this.arg$5 = z;
        this.arg$6 = i3;
        this.arg$7 = i4;
    }

    public void run() {
        this.arg$1.lambda$loadMediaDatabase$63$DataQuery(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7);
    }
}
