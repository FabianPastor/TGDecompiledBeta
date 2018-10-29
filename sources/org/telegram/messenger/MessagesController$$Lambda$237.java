package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MessagesController$$Lambda$237 implements Runnable {
    private final MessagesController arg$1;
    private final TLObject arg$2;
    private final User arg$3;
    private final int arg$4;

    MessagesController$$Lambda$237(MessagesController messagesController, TLObject tLObject, User user, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = tLObject;
        this.arg$3 = user;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$null$16$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}
