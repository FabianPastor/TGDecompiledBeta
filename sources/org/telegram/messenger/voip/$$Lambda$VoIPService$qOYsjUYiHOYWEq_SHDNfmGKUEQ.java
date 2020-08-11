package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$qOYsjUYiHOYWEq_-SHDNfmGKUEQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$qOYsjUYiHOYWEq_SHDNfmGKUEQ implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$qOYsjUYiHOYWEq_SHDNfmGKUEQ INSTANCE = new $$Lambda$VoIPService$qOYsjUYiHOYWEq_SHDNfmGKUEQ();

    private /* synthetic */ $$Lambda$VoIPService$qOYsjUYiHOYWEq_SHDNfmGKUEQ() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$22(tLObject, tLRPC$TL_error);
    }
}
