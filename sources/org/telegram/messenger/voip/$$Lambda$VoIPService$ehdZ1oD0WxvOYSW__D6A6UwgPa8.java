package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$ehdZ1oD0WxvOYSW__D6A6UwgPa8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$ehdZ1oD0WxvOYSW__D6A6UwgPa8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$ehdZ1oD0WxvOYSW__D6A6UwgPa8 INSTANCE = new $$Lambda$VoIPService$ehdZ1oD0WxvOYSW__D6A6UwgPa8();

    private /* synthetic */ $$Lambda$VoIPService$ehdZ1oD0WxvOYSW__D6A6UwgPa8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$44(tLObject, tLRPC$TL_error);
    }
}
