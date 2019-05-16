package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$8gQkjP_exgY3T8Zm64H9fCFaXkg implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$8gQkjP_exgY3T8Zm64H9fCFaXkg(SendMessagesHelper sendMessagesHelper, Message message) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
    }

    public final void run() {
        this.f$0.lambda$null$30$SendMessagesHelper(this.f$1);
    }
}
