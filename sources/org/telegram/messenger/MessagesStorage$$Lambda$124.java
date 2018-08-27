package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class MessagesStorage$$Lambda$124 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final long arg$3;
    private final TLObject arg$4;

    MessagesStorage$$Lambda$124(MessagesStorage messagesStorage, int i, long j, TLObject tLObject) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = j;
        this.arg$4 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$13$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
