package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$jV9Gq3Q6H66Q6pHxnGZUWi6v2TA implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$jV9Gq3Q6H66Q6pHxnGZUWi6v2TA(SendMessagesHelper sendMessagesHelper, Message message, int i, long j, int i2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = j;
        this.f$4 = i2;
    }

    public final void run() {
        this.f$0.lambda$null$25$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
