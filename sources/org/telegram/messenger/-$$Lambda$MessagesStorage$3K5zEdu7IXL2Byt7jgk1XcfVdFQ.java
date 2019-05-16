package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$3K5zEdu7IXL2Byt7jgk1XcfVdFQ implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ Message f$1;

    public /* synthetic */ -$$Lambda$MessagesStorage$3K5zEdu7IXL2Byt7jgk1XcfVdFQ(MessagesStorage messagesStorage, Message message) {
        this.f$0 = messagesStorage;
        this.f$1 = message;
    }

    public final void run() {
        this.f$0.lambda$markMessageAsSendError$121$MessagesStorage(this.f$1);
    }
}
