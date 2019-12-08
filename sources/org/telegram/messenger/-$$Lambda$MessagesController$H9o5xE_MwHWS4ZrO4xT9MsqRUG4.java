package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_config;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$H9o5xE_MwHWS4ZrO4xT9MsqRUG4 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_config f$1;

    public /* synthetic */ -$$Lambda$MessagesController$H9o5xE_MwHWS4ZrO4xT9MsqRUG4(MessagesController messagesController, TL_config tL_config) {
        this.f$0 = messagesController;
        this.f$1 = tL_config;
    }

    public final void run() {
        this.f$0.lambda$updateConfig$6$MessagesController(this.f$1);
    }
}
