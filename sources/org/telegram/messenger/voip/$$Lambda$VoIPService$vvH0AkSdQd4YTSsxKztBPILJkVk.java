package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$vvH0AkSdQd4YTSsxKztBPILJkVk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$vvH0AkSdQd4YTSsxKztBPILJkVk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$vvH0AkSdQd4YTSsxKztBPILJkVk INSTANCE = new $$Lambda$VoIPService$vvH0AkSdQd4YTSsxKztBPILJkVk();

    private /* synthetic */ $$Lambda$VoIPService$vvH0AkSdQd4YTSsxKztBPILJkVk() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onTgVoipStop$3(tLObject, tLRPC$TL_error);
    }
}