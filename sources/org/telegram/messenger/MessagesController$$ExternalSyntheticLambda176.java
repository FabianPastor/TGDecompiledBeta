package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$updates_ChannelDifference;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda176 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$updates_ChannelDifference f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda176(MessagesController messagesController, TLRPC$updates_ChannelDifference tLRPC$updates_ChannelDifference) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$updates_ChannelDifference;
    }

    public final void run() {
        this.f$0.lambda$getChannelDifference$245(this.f$1);
    }
}