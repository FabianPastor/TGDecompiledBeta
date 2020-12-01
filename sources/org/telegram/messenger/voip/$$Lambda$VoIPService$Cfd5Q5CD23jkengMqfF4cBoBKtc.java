package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$Cfd5Q5CD23jkengMqfF4cBoBKtc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$Cfd5Q5CD23jkengMqfF4cBoBKtc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$Cfd5Q5CD23jkengMqfF4cBoBKtc INSTANCE = new $$Lambda$VoIPService$Cfd5Q5CD23jkengMqfF4cBoBKtc();

    private /* synthetic */ $$Lambda$VoIPService$Cfd5Q5CD23jkengMqfF4cBoBKtc() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$38(tLObject, tLRPC$TL_error);
    }
}
