package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$PdW_pKJVWk0744XRiNGlh7K_NuI implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Updates f$1;
    private final /* synthetic */ Message f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$PdW_pKJVWk0744XRiNGlh7K_NuI(SendMessagesHelper sendMessagesHelper, Updates updates, Message message, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = updates;
        this.f$2 = message;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$null$33$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
    }
}
