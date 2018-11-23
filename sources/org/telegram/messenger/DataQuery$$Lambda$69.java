package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

final /* synthetic */ class DataQuery$$Lambda$69 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;
    private final Message arg$3;

    DataQuery$$Lambda$69(DataQuery dataQuery, long j, Message message) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
        this.arg$3 = message;
    }

    public void run() {
        this.arg$1.lambda$putBotKeyboard$109$DataQuery(this.arg$2, this.arg$3);
    }
}
