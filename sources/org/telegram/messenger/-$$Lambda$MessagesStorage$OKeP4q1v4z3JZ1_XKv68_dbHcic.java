package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.ChatFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$OKeP4q1v4z3JZ1_XKv68_dbHcic implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ ChatFull f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$OKeP4q1v4z3JZ1_XKv68_dbHcic(MessagesStorage messagesStorage, ChatFull chatFull, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = chatFull;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$updateChatInfo$72$MessagesStorage(this.f$1, this.f$2);
    }
}