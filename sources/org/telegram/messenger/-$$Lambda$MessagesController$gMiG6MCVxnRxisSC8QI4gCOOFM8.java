package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateChannel;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$gMiG6MCVxnRxisSC8QI4gCOOFM8 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_updateChannel f$1;

    public /* synthetic */ -$$Lambda$MessagesController$gMiG6MCVxnRxisSC8QI4gCOOFM8(MessagesController messagesController, TL_updateChannel tL_updateChannel) {
        this.f$0 = messagesController;
        this.f$1 = tL_updateChannel;
    }

    public final void run() {
        this.f$0.lambda$null$247$MessagesController(this.f$1);
    }
}
