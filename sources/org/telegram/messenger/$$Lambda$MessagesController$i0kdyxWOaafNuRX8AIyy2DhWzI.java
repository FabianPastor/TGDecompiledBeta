package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$i0kdyxWOaafNuRX8A-Iyy2DhWzI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$i0kdyxWOaafNuRX8AIyy2DhWzI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$i0kdyxWOaafNuRX8AIyy2DhWzI INSTANCE = new $$Lambda$MessagesController$i0kdyxWOaafNuRX8AIyy2DhWzI();

    private /* synthetic */ $$Lambda$MessagesController$i0kdyxWOaafNuRX8AIyy2DhWzI() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$deleteUserPhoto$78(tLObject, tLRPC$TL_error);
    }
}
