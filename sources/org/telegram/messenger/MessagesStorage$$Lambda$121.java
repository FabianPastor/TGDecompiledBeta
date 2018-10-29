package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.TL_dialog;

final /* synthetic */ class MessagesStorage$$Lambda$121 implements Runnable {
    private final MessagesStorage arg$1;
    private final TL_dialog arg$2;
    private final InputPeer arg$3;
    private final long arg$4;

    MessagesStorage$$Lambda$121(MessagesStorage messagesStorage, TL_dialog tL_dialog, InputPeer inputPeer, long j) {
        this.arg$1 = messagesStorage;
        this.arg$2 = tL_dialog;
        this.arg$3 = inputPeer;
        this.arg$4 = j;
    }

    public void run() {
        this.arg$1.lambda$null$10$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
