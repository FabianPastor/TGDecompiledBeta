package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda91 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC.UserFull f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda91(MessagesStorage messagesStorage, boolean z, TLRPC.UserFull userFull) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = userFull;
    }

    public final void run() {
        this.f$0.m1088lambda$updateUserInfo$90$orgtelegrammessengerMessagesStorage(this.f$1, this.f$2);
    }
}
