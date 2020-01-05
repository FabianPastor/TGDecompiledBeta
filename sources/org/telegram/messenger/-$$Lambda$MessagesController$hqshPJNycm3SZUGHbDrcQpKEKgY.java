package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$hqshPJNycm3SZUGHbDrcQpKEKgY implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesController$hqshPJNycm3SZUGHbDrcQpKEKgY(MessagesController messagesController, TL_error tL_error, int i) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$null$220$MessagesController(this.f$1, this.f$2);
    }
}
