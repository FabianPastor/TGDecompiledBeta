package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateNewMessage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$1N98oIZfO72oZTruBKvG29-QI2s implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_updateNewMessage f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$1N98oIZfO72oZTruBKvG29-QI2s(SendMessagesHelper sendMessagesHelper, TL_updateNewMessage tL_updateNewMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_updateNewMessage;
    }

    public final void run() {
        this.f$0.lambda$null$36$SendMessagesHelper(this.f$1);
    }
}