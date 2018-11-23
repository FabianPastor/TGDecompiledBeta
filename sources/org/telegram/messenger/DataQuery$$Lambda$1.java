package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

final /* synthetic */ class DataQuery$$Lambda$1 implements Runnable {
    private final DataQuery arg$1;
    private final int arg$2;
    private final Document arg$3;

    DataQuery$$Lambda$1(DataQuery dataQuery, int i, Document document) {
        this.arg$1 = dataQuery;
        this.arg$2 = i;
        this.arg$3 = document;
    }

    public void run() {
        this.arg$1.lambda$addRecentSticker$1$DataQuery(this.arg$2, this.arg$3);
    }
}
