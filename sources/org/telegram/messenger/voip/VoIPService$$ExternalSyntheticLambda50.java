package org.telegram.messenger.voip;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda50 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC.TL_groupCallParticipant f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda50(VoIPService voIPService, TLRPC.TL_groupCallParticipant tL_groupCallParticipant) {
        this.f$0 = voIPService;
        this.f$1 = tL_groupCallParticipant;
    }

    public final void run() {
        this.f$0.m1208lambda$startGroupCall$25$orgtelegrammessengervoipVoIPService(this.f$1);
    }
}
