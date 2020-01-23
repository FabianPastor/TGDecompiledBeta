package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$Z-r73k_daSxgwkVfswtomRn77RE implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ Message f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ DelayedMessage f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$Z-r73k_daSxgwkVfswtomRn77RE(SendMessagesHelper sendMessagesHelper, Message message, boolean z, TLObject tLObject, DelayedMessage delayedMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = message;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = delayedMessage;
    }

    public final void run() {
        this.f$0.lambda$null$39$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
