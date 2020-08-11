package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$15rkLbQM2_f6CPdXQr1yBt9ytr4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$15rkLbQM2_f6CPdXQr1yBt9ytr4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$15rkLbQM2_f6CPdXQr1yBt9ytr4 INSTANCE = new $$Lambda$VoIPService$15rkLbQM2_f6CPdXQr1yBt9ytr4();

    private /* synthetic */ $$Lambda$VoIPService$15rkLbQM2_f6CPdXQr1yBt9ytr4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$23(tLObject, tLRPC$TL_error);
    }
}
