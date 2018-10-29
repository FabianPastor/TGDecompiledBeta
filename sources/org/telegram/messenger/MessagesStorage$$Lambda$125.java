package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputPeer;

final /* synthetic */ class MessagesStorage$$Lambda$125 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final InputPeer arg$3;
    private final long arg$4;

    MessagesStorage$$Lambda$125(MessagesStorage messagesStorage, long j, InputPeer inputPeer, long j2) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = inputPeer;
        this.arg$4 = j2;
    }

    public void run() {
        this.arg$1.lambda$null$14$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
