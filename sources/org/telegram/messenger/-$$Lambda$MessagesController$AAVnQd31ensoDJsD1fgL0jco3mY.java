package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$AAVnQd31ensoDJsD1fgL0jco3mY implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$MessagesController$AAVnQd31ensoDJsD1fgL0jco3mY(MessagesController messagesController, Chat chat) {
        this.f$0 = messagesController;
        this.f$1 = chat;
    }

    public final void run() {
        this.f$0.lambda$putChat$13$MessagesController(this.f$1);
    }
}
