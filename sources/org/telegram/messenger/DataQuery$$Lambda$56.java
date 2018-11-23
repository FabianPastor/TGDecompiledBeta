package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class DataQuery$$Lambda$56 implements Runnable {
    private final DataQuery arg$1;
    private final Message arg$2;

    DataQuery$$Lambda$56(DataQuery dataQuery, Message message) {
        this.arg$1 = dataQuery;
        this.arg$2 = message;
    }

    public void run() {
        this.arg$1.lambda$savePinnedMessage$88$DataQuery(this.arg$2);
    }
}
