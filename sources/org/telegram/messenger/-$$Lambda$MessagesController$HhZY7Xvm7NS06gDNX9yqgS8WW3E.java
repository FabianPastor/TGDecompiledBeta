package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$HhZY7Xvm7NS06gDNX9yqgS8WW3E implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ Chat f$2;

    public /* synthetic */ -$$Lambda$MessagesController$HhZY7Xvm7NS06gDNX9yqgS8WW3E(MessagesController messagesController, boolean z, Chat chat) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = chat;
    }

    public final void run() {
        this.f$0.lambda$startShortPoll$212$MessagesController(this.f$1, this.f$2);
    }
}
