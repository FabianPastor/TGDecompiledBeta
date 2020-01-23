package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$phF_ng_tGeU--e0n9Exxu09IoDQ implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Updates f$1;

    public /* synthetic */ -$$Lambda$MessagesController$phF_ng_tGeU--e0n9Exxu09IoDQ(MessagesController messagesController, Updates updates) {
        this.f$0 = messagesController;
        this.f$1 = updates;
    }

    public final void run() {
        this.f$0.lambda$null$173$MessagesController(this.f$1);
    }
}
