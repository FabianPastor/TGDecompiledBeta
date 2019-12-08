package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$UvWg5EWKqMLOh3fM1x4h7GFVQZ4 implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$UvWg5EWKqMLOh3fM1x4h7GFVQZ4(SendMessagesHelper sendMessagesHelper, Message message, int i, int i2, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$null$43$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
