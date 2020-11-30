package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$Ly1m8jkgnUROvar_LhNOvKlsJia4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$Ly1m8jkgnUROvar_LhNOvKlsJia4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$Ly1m8jkgnUROvar_LhNOvKlsJia4 INSTANCE = new $$Lambda$VoIPService$Ly1m8jkgnUROvar_LhNOvKlsJia4();

    private /* synthetic */ $$Lambda$VoIPService$Ly1m8jkgnUROvar_LhNOvKlsJia4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$37(tLObject, tLRPC$TL_error);
    }
}
