package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_config;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda161 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_config f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda161(MessagesController messagesController, TLRPC$TL_config tLRPC$TL_config) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_config;
    }

    public final void run() {
        this.f$0.lambda$updateConfig$23(this.f$1);
    }
}
