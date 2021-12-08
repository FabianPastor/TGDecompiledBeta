package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda82 implements RequestDelegate {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda82(VoIPService voIPService, int i) {
        this.f$0 = voIPService;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1271xe204004f(this.f$1, tLObject, tL_error);
    }
}
