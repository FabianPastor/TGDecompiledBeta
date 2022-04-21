package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda102 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.updates_ChannelDifference f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda102(MessagesController messagesController, TLRPC.updates_ChannelDifference updates_channeldifference) {
        this.f$0 = messagesController;
        this.f$1 = updates_channeldifference;
    }

    public final void run() {
        this.f$0.m209x38bdc6b8(this.f$1);
    }
}
