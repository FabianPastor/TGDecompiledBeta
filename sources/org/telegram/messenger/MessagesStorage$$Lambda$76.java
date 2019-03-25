package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_chatBannedRights;

final /* synthetic */ class MessagesStorage$$Lambda$76 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final int arg$3;
    private final TL_chatBannedRights arg$4;

    MessagesStorage$$Lambda$76(MessagesStorage messagesStorage, int i, int i2, TL_chatBannedRights tL_chatBannedRights) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = tL_chatBannedRights;
    }

    public void run() {
        this.arg$1.lambda$updateChatDefaultBannedRights$104$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
