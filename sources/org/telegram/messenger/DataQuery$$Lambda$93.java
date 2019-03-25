package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class DataQuery$$Lambda$93 implements Runnable {
    private final DataQuery arg$1;
    private final TLObject arg$2;

    DataQuery$$Lambda$93(DataQuery dataQuery, TLObject tLObject) {
        this.arg$1 = dataQuery;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$72$DataQuery(this.arg$2);
    }
}
