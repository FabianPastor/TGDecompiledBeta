package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.updates_ChannelDifference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$cgs0OFAJnqEUItiFpqM9coCMvSw implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ updates_ChannelDifference f$2;

    public /* synthetic */ -$$Lambda$MessagesController$cgs0OFAJnqEUItiFpqM9coCMvSw(MessagesController messagesController, ArrayList arrayList, updates_ChannelDifference updates_channeldifference) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = updates_channeldifference;
    }

    public final void run() {
        this.f$0.lambda$null$217$MessagesController(this.f$1, this.f$2);
    }
}
