package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.updates_Difference;

final /* synthetic */ class MessagesController$$Lambda$174 implements Runnable {
    private final MessagesController arg$1;
    private final updates_Difference arg$2;

    MessagesController$$Lambda$174(MessagesController messagesController, updates_Difference updates_difference) {
        this.arg$1 = messagesController;
        this.arg$2 = updates_difference;
    }

    public void run() {
        this.arg$1.lambda$null$204$MessagesController(this.arg$2);
    }
}
