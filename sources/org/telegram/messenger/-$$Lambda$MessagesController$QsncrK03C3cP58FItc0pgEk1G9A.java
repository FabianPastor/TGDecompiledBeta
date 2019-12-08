package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.ChatFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$QsncrK03C3cP58FItc0pgEk1G9A implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ChatFull f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$MessagesController$QsncrK03C3cP58FItc0pgEk1G9A(MessagesController messagesController, ChatFull chatFull, String str) {
        this.f$0 = messagesController;
        this.f$1 = chatFull;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$null$174$MessagesController(this.f$1, this.f$2);
    }
}
