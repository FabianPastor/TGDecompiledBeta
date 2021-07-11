package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.voip.-$$Lambda$VoIPService$WBMDFNgn-Wome4zSzwwhZjCJlXM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VoIPService$WBMDFNgnWome4zSzwwhZjCJlXM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$VoIPService$WBMDFNgnWome4zSzwwhZjCJlXM INSTANCE = new $$Lambda$VoIPService$WBMDFNgnWome4zSzwwhZjCJlXM();

    private /* synthetic */ $$Lambda$VoIPService$WBMDFNgnWome4zSzwwhZjCJlXM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        VoIPService.lambda$onSignalingData$55(tLObject, tLRPC$TL_error);
    }
}
