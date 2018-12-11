package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

final /* synthetic */ class DataQuery$$Lambda$3 implements Runnable {
    private final DataQuery arg$1;
    private final Document arg$2;

    DataQuery$$Lambda$3(DataQuery dataQuery, Document document) {
        this.arg$1 = dataQuery;
        this.arg$2 = document;
    }

    public void run() {
        this.arg$1.lambda$removeRecentGif$3$DataQuery(this.arg$2);
    }
}
