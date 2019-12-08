package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$QI9kvFFWyf5nFEU6c4W_RoaNIeY implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$MessagesController$QI9kvFFWyf5nFEU6c4W_RoaNIeY(MessagesController messagesController, Chat chat) {
        this.f$0 = messagesController;
        this.f$1 = chat;
    }

    public final void run() {
        this.f$0.lambda$putChat$21$MessagesController(this.f$1);
    }
}
