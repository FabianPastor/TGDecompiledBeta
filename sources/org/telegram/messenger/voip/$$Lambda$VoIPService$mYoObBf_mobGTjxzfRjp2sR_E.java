package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$mY-oObBf_mobGTj-xzfRjp2sR_E  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$mYoObBf_mobGTjxzfRjp2sR_E implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$mYoObBf_mobGTjxzfRjp2sR_E INSTANCE = new $$Lambda$VoIPService$mYoObBf_mobGTjxzfRjp2sR_E();

    private /* synthetic */ $$Lambda$VoIPService$mYoObBf_mobGTjxzfRjp2sR_E() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$52(tLObject, tLRPC$TL_error);
    }
}
