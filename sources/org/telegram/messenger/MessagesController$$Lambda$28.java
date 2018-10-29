package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MessagesController$$Lambda$28 implements RequestDelegate {
    private final MessagesController arg$1;
    private final User arg$2;

    MessagesController$$Lambda$28(MessagesController messagesController, User user) {
        this.arg$1 = messagesController;
        this.arg$2 = user;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$blockUser$37$MessagesController(this.arg$2, tLObject, tL_error);
    }
}
