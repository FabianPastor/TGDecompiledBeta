package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$-_A-zPdho0WiN66Q5g4H70UkDlI implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$MessagesController$-_A-zPdho0WiN66Q5g4H70UkDlI(MessagesController messagesController, Chat chat) {
        this.f$0 = messagesController;
        this.f$1 = chat;
    }

    public final void run() {
        this.f$0.lambda$null$266$MessagesController(this.f$1);
    }
}
