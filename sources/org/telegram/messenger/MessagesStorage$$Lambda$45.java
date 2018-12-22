package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_userFull;

final /* synthetic */ class MessagesStorage$$Lambda$45 implements Runnable {
    private final MessagesStorage arg$1;
    private final boolean arg$2;
    private final TL_userFull arg$3;

    MessagesStorage$$Lambda$45(MessagesStorage messagesStorage, boolean z, TL_userFull tL_userFull) {
        this.arg$1 = messagesStorage;
        this.arg$2 = z;
        this.arg$3 = tL_userFull;
    }

    public void run() {
        this.arg$1.lambda$updateUserInfo$66$MessagesStorage(this.arg$2, this.arg$3);
    }
}
