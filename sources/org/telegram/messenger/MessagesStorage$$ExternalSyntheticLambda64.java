package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda64 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.ChatFull f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda64(MessagesStorage messagesStorage, TLRPC.ChatFull chatFull) {
        this.f$0 = messagesStorage;
        this.f$1 = chatFull;
    }

    public final void run() {
        this.f$0.m2285lambda$updateChatInfo$106$orgtelegrammessengerMessagesStorage(this.f$1);
    }
}
