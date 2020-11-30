package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$7ccUBh9SFdmH5Mj8_5J4W9fRDdc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$7ccUBh9SFdmH5Mj8_5J4W9fRDdc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$7ccUBh9SFdmH5Mj8_5J4W9fRDdc INSTANCE = new $$Lambda$MessagesController$7ccUBh9SFdmH5Mj8_5J4W9fRDdc();

    private /* synthetic */ $$Lambda$MessagesController$7ccUBh9SFdmH5Mj8_5J4W9fRDdc() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteUserPhoto$82(tLObject, tLRPC$TL_error);
    }
}
