package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_poll;
import org.telegram.tgnet.TLRPC.TL_pollResults;

final /* synthetic */ class MessagesStorage$$Lambda$30 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final TL_poll arg$3;
    private final TL_pollResults arg$4;

    MessagesStorage$$Lambda$30(MessagesStorage messagesStorage, long j, TL_poll tL_poll, TL_pollResults tL_pollResults) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = tL_poll;
        this.arg$4 = tL_pollResults;
    }

    public void run() {
        this.arg$1.lambda$updateMessagePollResults$50$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
