package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Config;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda153 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Config f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda153(MessagesController messagesController, TLRPC$Config tLRPC$Config) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Config;
    }

    public final void run() {
        this.f$0.lambda$updateConfig$23(this.f$1);
    }
}
