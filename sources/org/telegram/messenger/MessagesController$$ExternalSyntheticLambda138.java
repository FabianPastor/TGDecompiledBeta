package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_config;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda138 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_config f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda138(MessagesController messagesController, TLRPC$TL_config tLRPC$TL_config) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_config;
    }

    public final void run() {
        this.f$0.lambda$updateConfig$17(this.f$1);
    }
}