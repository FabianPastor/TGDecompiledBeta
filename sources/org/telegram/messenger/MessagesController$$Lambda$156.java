package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateUserBlocked;

final /* synthetic */ class MessagesController$$Lambda$156 implements Runnable {
    private final MessagesController arg$1;
    private final TL_updateUserBlocked arg$2;

    MessagesController$$Lambda$156(MessagesController messagesController, TL_updateUserBlocked tL_updateUserBlocked) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_updateUserBlocked;
    }

    public void run() {
        this.arg$1.lambda$null$222$MessagesController(this.arg$2);
    }
}
