package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputPeer;

final /* synthetic */ class MessagesStorage$$Lambda$122 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final boolean arg$3;
    private final InputPeer arg$4;
    private final long arg$5;

    MessagesStorage$$Lambda$122(MessagesStorage messagesStorage, long j, boolean z, InputPeer inputPeer, long j2) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = z;
        this.arg$4 = inputPeer;
        this.arg$5 = j2;
    }

    public void run() {
        this.arg$1.lambda$null$11$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
