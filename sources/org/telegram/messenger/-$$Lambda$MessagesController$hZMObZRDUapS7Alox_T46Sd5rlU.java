package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$hZMObZRDUapS7Alox_T46Sd5rlU implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$hZMObZRDUapS7Alox_T46Sd5rlU INSTANCE = new -$$Lambda$MessagesController$hZMObZRDUapS7Alox_T46Sd5rlU();

    private /* synthetic */ -$$Lambda$MessagesController$hZMObZRDUapS7Alox_T46Sd5rlU() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$unblockUser$63(tLObject, tL_error);
    }
}
