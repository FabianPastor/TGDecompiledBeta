package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputChannel;

final /* synthetic */ class MessagesStorage$$Lambda$136 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final int arg$3;
    private final InputChannel arg$4;
    private final int arg$5;
    private final long arg$6;

    MessagesStorage$$Lambda$136(MessagesStorage messagesStorage, int i, int i2, InputChannel inputChannel, int i3, long j) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = inputChannel;
        this.arg$5 = i3;
        this.arg$6 = j;
    }

    public void run() {
        this.arg$1.lambda$null$15$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
