package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$44UwBrbc_0c8t7HE5nZu3rtwHxg implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$44UwBrbc_0c8t7HE5nZu3rtwHxg(SendMessagesHelper sendMessagesHelper, Message message, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$null$40$SendMessagesHelper(this.f$1, this.f$2);
    }
}
