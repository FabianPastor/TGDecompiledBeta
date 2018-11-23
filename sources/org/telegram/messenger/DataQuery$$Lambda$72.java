package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class DataQuery$$Lambda$72 implements Runnable {
    private final DataQuery arg$1;
    private final Message arg$2;
    private final long arg$3;

    DataQuery$$Lambda$72(DataQuery dataQuery, Message message, long j) {
        this.arg$1 = dataQuery;
        this.arg$2 = message;
        this.arg$3 = j;
    }

    public void run() {
        this.arg$1.lambda$null$105$DataQuery(this.arg$2, this.arg$3);
    }
}
