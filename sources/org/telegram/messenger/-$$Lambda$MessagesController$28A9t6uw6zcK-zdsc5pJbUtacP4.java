package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.updates_ChannelDifference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$28A9t6uw6zcK-zdsc5pJbUtacP4 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ updates_ChannelDifference f$1;

    public /* synthetic */ -$$Lambda$MessagesController$28A9t6uw6zcK-zdsc5pJbUtacP4(MessagesController messagesController, updates_ChannelDifference updates_channeldifference) {
        this.f$0 = messagesController;
        this.f$1 = updates_channeldifference;
    }

    public final void run() {
        this.f$0.lambda$null$197$MessagesController(this.f$1);
    }
}
