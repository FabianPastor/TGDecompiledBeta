package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$vUNqZAvkm5x_4-ROauvZnsrS8Ks  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$vUNqZAvkm5x_4ROauvZnsrS8Ks implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$vUNqZAvkm5x_4ROauvZnsrS8Ks INSTANCE = new $$Lambda$VoIPService$vUNqZAvkm5x_4ROauvZnsrS8Ks();

    private /* synthetic */ $$Lambda$VoIPService$vUNqZAvkm5x_4ROauvZnsrS8Ks() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$43(tLObject, tLRPC$TL_error);
    }
}
