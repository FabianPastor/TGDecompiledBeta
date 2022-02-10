package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda165 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda165(MessagesStorage messagesStorage, TLRPC$Message tLRPC$Message, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$Message;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$markMessageAsSendError$155(this.f$1, this.f$2);
    }
}
