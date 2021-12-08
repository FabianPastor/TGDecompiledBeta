package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda89 implements RequestDelegate {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda89(VoIPService voIPService, boolean z) {
        this.f$0 = voIPService;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1200xcd5054c9(this.f$1, tLObject, tL_error);
    }
}
