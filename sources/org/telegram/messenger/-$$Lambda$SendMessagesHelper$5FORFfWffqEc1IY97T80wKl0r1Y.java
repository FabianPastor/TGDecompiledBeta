package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$5FORFfWffqEc1IY97T80wKl0r1Y implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Updates f$1;
    private final /* synthetic */ Message f$2;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$5FORFfWffqEc1IY97T80wKl0r1Y(SendMessagesHelper sendMessagesHelper, Updates updates, Message message) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = updates;
        this.f$2 = message;
    }

    public final void run() {
        this.f$0.lambda$null$31$SendMessagesHelper(this.f$1, this.f$2);
    }
}
