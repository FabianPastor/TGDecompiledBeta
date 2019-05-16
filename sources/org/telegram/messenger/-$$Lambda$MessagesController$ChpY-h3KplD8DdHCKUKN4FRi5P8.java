package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$ChpY-h3KplD8DdHCKUKN4FRi5P8 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ Chat f$2;

    public /* synthetic */ -$$Lambda$MessagesController$ChpY-h3KplD8DdHCKUKN4FRi5P8(MessagesController messagesController, boolean z, Chat chat) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = chat;
    }

    public final void run() {
        this.f$0.lambda$startShortPoll$196$MessagesController(this.f$1, this.f$2);
    }
}
