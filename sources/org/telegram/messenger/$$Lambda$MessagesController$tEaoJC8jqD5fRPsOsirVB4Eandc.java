package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$tEaoJC8jqD5fRPsOsirVB4Eandc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$tEaoJC8jqD5fRPsOsirVB4Eandc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$tEaoJC8jqD5fRPsOsirVB4Eandc INSTANCE = new $$Lambda$MessagesController$tEaoJC8jqD5fRPsOsirVB4Eandc();

    private /* synthetic */ $$Lambda$MessagesController$tEaoJC8jqD5fRPsOsirVB4Eandc() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$263(tLObject, tLRPC$TL_error);
    }
}
