package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MessagesController$$Lambda$49 implements Runnable {
    private final MessagesController arg$1;
    private final User arg$2;
    private final TL_userFull arg$3;
    private final MessageObject arg$4;

    MessagesController$$Lambda$49(MessagesController messagesController, User user, TL_userFull tL_userFull, MessageObject messageObject) {
        this.arg$1 = messagesController;
        this.arg$2 = user;
        this.arg$3 = tL_userFull;
        this.arg$4 = messageObject;
    }

    public void run() {
        this.arg$1.lambda$processUserInfo$65$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}
