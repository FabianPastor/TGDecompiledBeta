package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class DataQuery$$Lambda$112 implements Runnable {
    private final DataQuery arg$1;
    private final TLObject arg$2;
    private final int arg$3;
    private final int arg$4;

    DataQuery$$Lambda$112(DataQuery dataQuery, TLObject tLObject, int i, int i2) {
        this.arg$1 = dataQuery;
        this.arg$2 = tLObject;
        this.arg$3 = i;
        this.arg$4 = i2;
    }

    public void run() {
        this.arg$1.lambda$null$34$DataQuery(this.arg$2, this.arg$3, this.arg$4);
    }
}
