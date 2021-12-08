package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLRPC$TL_messages_editChatAdmin;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda158 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_messages_editChatAdmin f$1;
    public final /* synthetic */ RequestDelegate f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda158(MessagesController messagesController, TLRPC$TL_messages_editChatAdmin tLRPC$TL_messages_editChatAdmin, RequestDelegate requestDelegate) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_messages_editChatAdmin;
        this.f$2 = requestDelegate;
    }

    public final void run() {
        this.f$0.lambda$setUserAdminRole$77(this.f$1, this.f$2);
    }
}
