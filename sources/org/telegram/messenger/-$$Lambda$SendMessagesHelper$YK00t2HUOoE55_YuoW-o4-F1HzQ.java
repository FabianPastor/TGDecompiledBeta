package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$YK00t2HUOoE55_YuoW-o4-F1HzQ implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$YK00t2HUOoE55_YuoW-o4-F1HzQ(SendMessagesHelper sendMessagesHelper, Message message) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
    }

    public final void run() {
        this.f$0.lambda$putToSendingMessages$23$SendMessagesHelper(this.f$1);
    }
}
