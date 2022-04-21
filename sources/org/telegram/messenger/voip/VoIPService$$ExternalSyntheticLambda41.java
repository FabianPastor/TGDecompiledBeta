package org.telegram.messenger.voip;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda41 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda41(VoIPService voIPService, TLObject tLObject, TLRPC.TL_error tL_error, boolean z) {
        this.f$0 = voIPService;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.m1150x40b029c8(this.f$1, this.f$2, this.f$3);
    }
}
