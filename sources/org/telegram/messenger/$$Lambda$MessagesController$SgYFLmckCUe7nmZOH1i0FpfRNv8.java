package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$SgYFLmckCUe7nmZOH1i0FpfRNv8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$SgYFLmckCUe7nmZOH1i0FpfRNv8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$SgYFLmckCUe7nmZOH1i0FpfRNv8 INSTANCE = new $$Lambda$MessagesController$SgYFLmckCUe7nmZOH1i0FpfRNv8();

    private /* synthetic */ $$Lambda$MessagesController$SgYFLmckCUe7nmZOH1i0FpfRNv8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$285(tLObject, tLRPC$TL_error);
    }
}
