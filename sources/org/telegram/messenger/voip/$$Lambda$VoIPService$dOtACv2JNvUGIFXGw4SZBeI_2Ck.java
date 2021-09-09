package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$dOtACv2JNvUGIFXGw4SZBeI_2Ck  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$dOtACv2JNvUGIFXGw4SZBeI_2Ck implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$dOtACv2JNvUGIFXGw4SZBeI_2Ck INSTANCE = new $$Lambda$VoIPService$dOtACv2JNvUGIFXGw4SZBeI_2Ck();

    private /* synthetic */ $$Lambda$VoIPService$dOtACv2JNvUGIFXGw4SZBeI_2Ck() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$58(tLObject, tLRPC$TL_error);
    }
}
