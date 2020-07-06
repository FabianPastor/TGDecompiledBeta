package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$IrSkYmGwSYbkyTxQ5Xrm7mGsKU4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$IrSkYmGwSYbkyTxQ5Xrm7mGsKU4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$IrSkYmGwSYbkyTxQ5Xrm7mGsKU4 INSTANCE = new $$Lambda$MessagesController$IrSkYmGwSYbkyTxQ5Xrm7mGsKU4();

    private /* synthetic */ $$Lambda$MessagesController$IrSkYmGwSYbkyTxQ5Xrm7mGsKU4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteUserPhoto$79(tLObject, tLRPC$TL_error);
    }
}
