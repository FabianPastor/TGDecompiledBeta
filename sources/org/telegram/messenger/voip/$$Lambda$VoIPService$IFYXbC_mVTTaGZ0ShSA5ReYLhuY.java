package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$IFYXbC_mVTTaGZ0ShSA5ReYLhuY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$IFYXbC_mVTTaGZ0ShSA5ReYLhuY implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$IFYXbC_mVTTaGZ0ShSA5ReYLhuY INSTANCE = new $$Lambda$VoIPService$IFYXbC_mVTTaGZ0ShSA5ReYLhuY();

    private /* synthetic */ $$Lambda$VoIPService$IFYXbC_mVTTaGZ0ShSA5ReYLhuY() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$54(tLObject, tLRPC$TL_error);
    }
}
