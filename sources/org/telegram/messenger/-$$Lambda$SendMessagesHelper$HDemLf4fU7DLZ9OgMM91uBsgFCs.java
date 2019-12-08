package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateShortSentMessage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$HDemLf4fU7DLZ9OgMM91uBsgFCs implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_updateShortSentMessage f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$HDemLf4fU7DLZ9OgMM91uBsgFCs(SendMessagesHelper sendMessagesHelper, TL_updateShortSentMessage tL_updateShortSentMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_updateShortSentMessage;
    }

    public final void run() {
        this.f$0.lambda$null$37$SendMessagesHelper(this.f$1);
    }
}
