package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda85 implements RequestDelegate {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda85(VoIPService voIPService, int i, Runnable runnable) {
        this.f$0 = voIPService;
        this.f$1 = i;
        this.f$2 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2463lambda$editCallMember$61$orgtelegrammessengervoipVoIPService(this.f$1, this.f$2, tLObject, tL_error);
    }
}
