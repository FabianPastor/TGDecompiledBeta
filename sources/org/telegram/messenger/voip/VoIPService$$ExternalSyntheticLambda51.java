package org.telegram.messenger.voip;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda51 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC.TL_updateGroupCall f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda51(VoIPService voIPService, TLRPC.TL_updateGroupCall tL_updateGroupCall) {
        this.f$0 = voIPService;
        this.f$1 = tL_updateGroupCall;
    }

    public final void run() {
        this.f$0.m2491lambda$startGroupCall$20$orgtelegrammessengervoipVoIPService(this.f$1);
    }
}
