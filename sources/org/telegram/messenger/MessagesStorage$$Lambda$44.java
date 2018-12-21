package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MessagesStorage$$Lambda$44 implements Runnable {
    private final MessagesStorage arg$1;
    private final User arg$2;
    private final boolean arg$3;
    private final int arg$4;

    MessagesStorage$$Lambda$44(MessagesStorage messagesStorage, User user, boolean z, int i) {
        this.arg$1 = messagesStorage;
        this.arg$2 = user;
        this.arg$3 = z;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$loadUserInfo$65$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
