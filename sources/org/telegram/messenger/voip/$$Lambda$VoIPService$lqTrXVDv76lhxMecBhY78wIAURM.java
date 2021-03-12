package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$lqTrXVDv76lhxMecBhY78wIAURM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$lqTrXVDv76lhxMecBhY78wIAURM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$lqTrXVDv76lhxMecBhY78wIAURM INSTANCE = new $$Lambda$VoIPService$lqTrXVDv76lhxMecBhY78wIAURM();

    private /* synthetic */ $$Lambda$VoIPService$lqTrXVDv76lhxMecBhY78wIAURM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$callFailed$51(tLObject, tLRPC$TL_error);
    }
}
