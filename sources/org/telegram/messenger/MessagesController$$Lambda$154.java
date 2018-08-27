package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateUserBlocked;

final /* synthetic */ class MessagesController$$Lambda$154 implements Runnable {
    private final MessagesController arg$1;
    private final TL_updateUserBlocked arg$2;

    MessagesController$$Lambda$154(MessagesController messagesController, TL_updateUserBlocked tL_updateUserBlocked) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_updateUserBlocked;
    }

    public void run() {
        this.arg$1.lambda$null$221$MessagesController(this.arg$2);
    }
}
