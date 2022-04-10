package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda143 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda143(MessagesController messagesController, TLRPC$Chat tLRPC$Chat, boolean z, int i) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Chat;
        this.f$2 = z;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$startShortPoll$256(this.f$1, this.f$2, this.f$3);
    }
}
