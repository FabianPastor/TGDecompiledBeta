package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$w9slh4U-GBm5r4g2PjGO5nLTaa4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$w9slh4UGBm5r4g2PjGO5nLTaa4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$w9slh4UGBm5r4g2PjGO5nLTaa4 INSTANCE = new $$Lambda$MessagesController$w9slh4UGBm5r4g2PjGO5nLTaa4();

    private /* synthetic */ $$Lambda$MessagesController$w9slh4UGBm5r4g2PjGO5nLTaa4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePromoDialog$92(tLObject, tLRPC$TL_error);
    }
}
