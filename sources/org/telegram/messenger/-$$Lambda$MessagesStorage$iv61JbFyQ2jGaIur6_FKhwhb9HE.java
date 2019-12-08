package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$iv61JbFyQ2jGaIur6_FKhwhb9HE implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$iv61JbFyQ2jGaIur6_FKhwhb9HE(MessagesStorage messagesStorage, Message message, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = message;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$markMessageAsSendError$120$MessagesStorage(this.f$1, this.f$2);
    }
}
