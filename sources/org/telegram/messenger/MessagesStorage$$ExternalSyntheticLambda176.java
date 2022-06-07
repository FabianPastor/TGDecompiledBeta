package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Vector;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda176 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$Vector f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda176(MessagesStorage messagesStorage, TLRPC$Vector tLRPC$Vector) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$Vector;
    }

    public final void run() {
        this.f$0.lambda$checkLoadedRemoteFilters$43(this.f$1);
    }
}
