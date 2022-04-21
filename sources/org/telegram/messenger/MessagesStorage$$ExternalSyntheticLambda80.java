package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda80 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.User f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda80(MessagesStorage messagesStorage, TLRPC.User user, boolean z, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = user;
        this.f$2 = z;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.m937lambda$loadUserInfo$92$orgtelegrammessengerMessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
