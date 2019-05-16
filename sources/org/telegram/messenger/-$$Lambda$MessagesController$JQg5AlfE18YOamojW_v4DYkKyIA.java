package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$JQg5AlfE18YOamojW_v4DYkKyIA implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$MessagesController$JQg5AlfE18YOamojW_v4DYkKyIA(MessagesController messagesController, Chat chat) {
        this.f$0 = messagesController;
        this.f$1 = chat;
    }

    public final void run() {
        this.f$0.lambda$putChat$11$MessagesController(this.f$1);
    }
}
