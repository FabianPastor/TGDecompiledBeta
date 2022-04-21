package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda63 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.ChatFull f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda63(MessagesStorage messagesStorage, TLRPC.ChatFull chatFull, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = chatFull;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.m1004lambda$updateChatInfo$96$orgtelegrammessengerMessagesStorage(this.f$1, this.f$2);
    }
}
