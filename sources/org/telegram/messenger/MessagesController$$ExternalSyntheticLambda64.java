package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda64 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_messages_editChatAdmin f$1;
    public final /* synthetic */ RequestDelegate f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda64(MessagesController messagesController, TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin, RequestDelegate requestDelegate) {
        this.f$0 = messagesController;
        this.f$1 = tL_messages_editChatAdmin;
        this.f$2 = requestDelegate;
    }

    public final void run() {
        this.f$0.m394xvar_cf0(this.f$1, this.f$2);
    }
}
