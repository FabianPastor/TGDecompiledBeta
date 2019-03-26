package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;

final /* synthetic */ class MessagesController$$Lambda$142 implements Runnable {
    private final MessagesController arg$1;
    private final TL_updateServiceNotification arg$2;

    MessagesController$$Lambda$142(MessagesController messagesController, TL_updateServiceNotification tL_updateServiceNotification) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_updateServiceNotification;
    }

    public void run() {
        this.arg$1.lambda$processUpdateArray$239$MessagesController(this.arg$2);
    }
}
