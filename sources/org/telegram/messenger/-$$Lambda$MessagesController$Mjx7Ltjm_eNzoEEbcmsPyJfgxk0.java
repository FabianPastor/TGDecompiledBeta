package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$Mjx7Ltjm_eNzoEEbcmsPyJfgxk0 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Chat f$1;
    private final /* synthetic */ User f$2;

    public /* synthetic */ -$$Lambda$MessagesController$Mjx7Ltjm_eNzoEEbcmsPyJfgxk0(MessagesController messagesController, Chat chat, User user) {
        this.f$0 = messagesController;
        this.f$1 = chat;
        this.f$2 = user;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$deleteUserChannelHistory$68$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
