package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputPeer;

final /* synthetic */ class MessagesStorage$$Lambda$137 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final boolean arg$3;
    private final int arg$4;
    private final int arg$5;
    private final boolean arg$6;
    private final InputPeer arg$7;
    private final long arg$8;

    MessagesStorage$$Lambda$137(MessagesStorage messagesStorage, long j, boolean z, int i, int i2, boolean z2, InputPeer inputPeer, long j2) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = z;
        this.arg$4 = i;
        this.arg$5 = i2;
        this.arg$6 = z2;
        this.arg$7 = inputPeer;
        this.arg$8 = j2;
    }

    public void run() {
        this.arg$1.lambda$null$17$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
