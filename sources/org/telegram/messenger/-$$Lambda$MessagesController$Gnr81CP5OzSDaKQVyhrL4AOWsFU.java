package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateUserBlocked;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$Gnr81CP5OzSDaKQVyhrL4AOWsFU implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_updateUserBlocked f$1;

    public /* synthetic */ -$$Lambda$MessagesController$Gnr81CP5OzSDaKQVyhrL4AOWsFU(MessagesController messagesController, TL_updateUserBlocked tL_updateUserBlocked) {
        this.f$0 = messagesController;
        this.f$1 = tL_updateUserBlocked;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$259$MessagesController(this.f$1);
    }
}
