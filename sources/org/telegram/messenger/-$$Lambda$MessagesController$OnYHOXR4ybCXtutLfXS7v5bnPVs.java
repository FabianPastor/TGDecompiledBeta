package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$OnYHOXR4ybCXtutLfXS7v5bnPVs implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_messages_editChatAdmin f$1;
    private final /* synthetic */ RequestDelegate f$2;

    public /* synthetic */ -$$Lambda$MessagesController$OnYHOXR4ybCXtutLfXS7v5bnPVs(MessagesController messagesController, TL_messages_editChatAdmin tL_messages_editChatAdmin, RequestDelegate requestDelegate) {
        this.f$0 = messagesController;
        this.f$1 = tL_messages_editChatAdmin;
        this.f$2 = requestDelegate;
    }

    public final void run() {
        this.f$0.lambda$setUserAdminRole$56$MessagesController(this.f$1, this.f$2);
    }
}
