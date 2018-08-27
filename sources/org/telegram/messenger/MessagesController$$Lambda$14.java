package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MessagesController$$Lambda$14 implements RequestDelegate {
    private final MessagesController arg$1;
    private final User arg$2;
    private final int arg$3;

    MessagesController$$Lambda$14(MessagesController messagesController, User user, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = user;
        this.arg$3 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadFullUser$18$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
