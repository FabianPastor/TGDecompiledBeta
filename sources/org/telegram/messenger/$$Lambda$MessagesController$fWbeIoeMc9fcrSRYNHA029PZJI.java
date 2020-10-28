package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$fWbeIoeMc9fcrSRYNHA029-PZJI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$fWbeIoeMc9fcrSRYNHA029PZJI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$fWbeIoeMc9fcrSRYNHA029PZJI INSTANCE = new $$Lambda$MessagesController$fWbeIoeMc9fcrSRYNHA029PZJI();

    private /* synthetic */ $$Lambda$MessagesController$fWbeIoeMc9fcrSRYNHA029PZJI() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$installTheme$82(tLObject, tLRPC$TL_error);
    }
}
