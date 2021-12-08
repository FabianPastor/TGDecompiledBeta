package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_messageReactions;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda45 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC$TL_messageReactions f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda45(MessagesStorage messagesStorage, int i, long j, TLRPC$TL_messageReactions tLRPC$TL_messageReactions) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = tLRPC$TL_messageReactions;
    }

    public final void run() {
        this.f$0.lambda$updateMessageReactions$71(this.f$1, this.f$2, this.f$3);
    }
}
