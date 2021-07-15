package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$GJshKy2Z_31whKvb6-8x-ts0VyU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$GJshKy2Z_31whKvb68xts0VyU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$GJshKy2Z_31whKvb68xts0VyU INSTANCE = new $$Lambda$VoIPService$GJshKy2Z_31whKvb68xts0VyU();

    private /* synthetic */ $$Lambda$VoIPService$GJshKy2Z_31whKvb68xts0VyU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$75(tLObject, tLRPC$TL_error);
    }
}
