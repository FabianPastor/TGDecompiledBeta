package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$DLxhb96Kw8VYNNLRvUQSa0phGO4 implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$DLxhb96Kw8VYNNLRvUQSa0phGO4(SendMessagesHelper sendMessagesHelper, Message message, int i) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$null$12$SendMessagesHelper(this.f$1, this.f$2);
    }
}
