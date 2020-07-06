package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$wDUQa7AQdz0TEqKTD3936W85PrE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$wDUQa7AQdz0TEqKTD3936W85PrE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$wDUQa7AQdz0TEqKTD3936W85PrE INSTANCE = new $$Lambda$MessagesController$wDUQa7AQdz0TEqKTD3936W85PrE();

    private /* synthetic */ $$Lambda$MessagesController$wDUQa7AQdz0TEqKTD3936W85PrE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePromoDialog$93(tLObject, tLRPC$TL_error);
    }
}
