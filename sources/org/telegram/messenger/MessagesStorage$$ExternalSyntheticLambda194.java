package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$UserFull;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda194 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC$UserFull f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda194(MessagesStorage messagesStorage, boolean z, TLRPC$UserFull tLRPC$UserFull) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = tLRPC$UserFull;
    }

    public final void run() {
        this.f$0.lambda$updateUserInfo$99(this.f$1, this.f$2);
    }
}
