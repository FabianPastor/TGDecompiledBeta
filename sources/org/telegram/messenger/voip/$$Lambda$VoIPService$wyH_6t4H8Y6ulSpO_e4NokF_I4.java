package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$wyH_6-t4H8Y6ulSpO_e4NokF_I4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$wyH_6t4H8Y6ulSpO_e4NokF_I4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$wyH_6t4H8Y6ulSpO_e4NokF_I4 INSTANCE = new $$Lambda$VoIPService$wyH_6t4H8Y6ulSpO_e4NokF_I4();

    private /* synthetic */ $$Lambda$VoIPService$wyH_6t4H8Y6ulSpO_e4NokF_I4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$50(tLObject, tLRPC$TL_error);
    }
}
