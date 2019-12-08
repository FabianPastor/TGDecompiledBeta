package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.updates_Difference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$j30_vRokDUWjGxcUsZfUTuvLGXA implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ updates_Difference f$1;

    public /* synthetic */ -$$Lambda$MessagesController$j30_vRokDUWjGxcUsZfUTuvLGXA(MessagesController messagesController, updates_Difference updates_difference) {
        this.f$0 = messagesController;
        this.f$1 = updates_difference;
    }

    public final void run() {
        this.f$0.lambda$null$223$MessagesController(this.f$1);
    }
}
