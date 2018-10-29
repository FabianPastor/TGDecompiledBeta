package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputChannel;

final /* synthetic */ class MessagesStorage$$Lambda$123 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final int arg$3;
    private final long arg$4;
    private final InputChannel arg$5;

    MessagesStorage$$Lambda$123(MessagesStorage messagesStorage, int i, int i2, long j, InputChannel inputChannel) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = j;
        this.arg$5 = inputChannel;
    }

    public void run() {
        this.arg$1.lambda$null$12$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
