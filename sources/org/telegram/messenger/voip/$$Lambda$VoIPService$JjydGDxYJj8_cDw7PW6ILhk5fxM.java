package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$JjydGDxYJj8_cDw7PW6ILhk5fxM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$JjydGDxYJj8_cDw7PW6ILhk5fxM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$JjydGDxYJj8_cDw7PW6ILhk5fxM INSTANCE = new $$Lambda$VoIPService$JjydGDxYJj8_cDw7PW6ILhk5fxM();

    private /* synthetic */ $$Lambda$VoIPService$JjydGDxYJj8_cDw7PW6ILhk5fxM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$54(tLObject, tLRPC$TL_error);
    }
}
