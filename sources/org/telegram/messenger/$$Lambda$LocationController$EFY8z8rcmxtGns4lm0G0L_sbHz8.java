package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$LocationController$EFY8z8rcmxtGns4lm0G0L_sbHz8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LocationController$EFY8z8rcmxtGns4lm0G0L_sbHz8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$LocationController$EFY8z8rcmxtGns4lm0G0L_sbHz8 INSTANCE = new $$Lambda$LocationController$EFY8z8rcmxtGns4lm0G0L_sbHz8();

    private /* synthetic */ $$Lambda$LocationController$EFY8z8rcmxtGns4lm0G0L_sbHz8() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        LocationController.lambda$broadcastLastKnownLocation$7(tLObject, tL_error);
    }
}
