package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateNewMessage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$KnZaYqmrVc-tQGTwKl5NtVyQ2iU implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_updateNewMessage f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$KnZaYqmrVc-tQGTwKl5NtVyQ2iU(SendMessagesHelper sendMessagesHelper, TL_updateNewMessage tL_updateNewMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_updateNewMessage;
    }

    public final void run() {
        this.f$0.lambda$null$44$SendMessagesHelper(this.f$1);
    }
}