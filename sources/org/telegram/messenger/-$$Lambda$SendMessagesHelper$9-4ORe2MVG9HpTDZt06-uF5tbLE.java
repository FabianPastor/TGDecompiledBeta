package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$9-4ORe2MVG9HpTDZt06-uF5tbLE implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$9-4ORe2MVG9HpTDZt06-uF5tbLE(SendMessagesHelper sendMessagesHelper, Message message, int i) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$null$13$SendMessagesHelper(this.f$1, this.f$2);
    }
}
