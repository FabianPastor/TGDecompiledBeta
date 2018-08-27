package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MessagesController$$Lambda$105 implements RequestDelegate {
    private final MessagesController arg$1;
    private final User arg$2;
    private final int arg$3;
    private final boolean arg$4;
    private final InputUser arg$5;

    MessagesController$$Lambda$105(MessagesController messagesController, User user, int i, boolean z, InputUser inputUser) {
        this.arg$1 = messagesController;
        this.arg$2 = user;
        this.arg$3 = i;
        this.arg$4 = z;
        this.arg$5 = inputUser;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$deleteUserFromChat$164$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, tLObject, tL_error);
    }
}
