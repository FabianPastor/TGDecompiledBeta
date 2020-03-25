package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$1BA73Tqpn3TDFxZL0d2b3Q7IVjI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$1BA73Tqpn3TDFxZL0d2b3Q7IVjI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$1BA73Tqpn3TDFxZL0d2b3Q7IVjI INSTANCE = new $$Lambda$VoIPService$1BA73Tqpn3TDFxZL0d2b3Q7IVjI();

    private /* synthetic */ $$Lambda$VoIPService$1BA73Tqpn3TDFxZL0d2b3Q7IVjI() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onTgVoipStop$1(tLObject, tLRPC$TL_error);
    }
}
