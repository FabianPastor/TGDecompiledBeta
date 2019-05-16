package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.UserFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$6oEVGdM2xXW3N6wtIglBSuDFLWE implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ UserFull f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$6oEVGdM2xXW3N6wtIglBSuDFLWE(MessagesStorage messagesStorage, int i, UserFull userFull) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = userFull;
    }

    public final void run() {
        this.f$0.lambda$null$76$MessagesStorage(this.f$1, this.f$2);
    }
}
