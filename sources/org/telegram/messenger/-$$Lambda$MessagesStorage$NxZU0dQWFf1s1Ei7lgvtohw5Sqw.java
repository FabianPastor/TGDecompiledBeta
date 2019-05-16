package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.ChatFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$NxZU0dQWFf1s1Ei7lgvtohw5Sqw implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ChatFull f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$NxZU0dQWFf1s1Ei7lgvtohw5Sqw(MessagesStorage messagesStorage, ChatFull chatFull) {
        this.f$0 = messagesStorage;
        this.f$1 = chatFull;
    }

    public final void run() {
        this.f$0.lambda$null$79$MessagesStorage(this.f$1);
    }
}
