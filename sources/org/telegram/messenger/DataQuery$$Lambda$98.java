package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.messages_Messages;

final /* synthetic */ class DataQuery$$Lambda$98 implements Runnable {
    private final DataQuery arg$1;
    private final messages_Messages arg$2;

    DataQuery$$Lambda$98(DataQuery dataQuery, messages_Messages messages_messages) {
        this.arg$1 = dataQuery;
        this.arg$2 = messages_messages;
    }

    public void run() {
        this.arg$1.lambda$null$57$DataQuery(this.arg$2);
    }
}
