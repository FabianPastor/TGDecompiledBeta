package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$pHB6phU-R4MZOR8xYVgJRVs67gg implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$MessagesController$pHB6phU-R4MZOR8xYVgJRVs67gg(MessagesController messagesController, Chat chat) {
        this.f$0 = messagesController;
        this.f$1 = chat;
    }

    public final void run() {
        this.f$0.lambda$putChat$15$MessagesController(this.f$1);
    }
}
