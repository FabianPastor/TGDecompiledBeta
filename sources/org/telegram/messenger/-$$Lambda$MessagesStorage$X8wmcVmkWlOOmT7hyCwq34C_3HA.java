package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$X8wmcVmkWlOOmT7hyCwq34C_3HA implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ Chat f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$X8wmcVmkWlOOmT7hyCwq34C_3HA(MessagesStorage messagesStorage, Chat chat, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = chat;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$null$8$MessagesStorage(this.f$1, this.f$2);
    }
}
