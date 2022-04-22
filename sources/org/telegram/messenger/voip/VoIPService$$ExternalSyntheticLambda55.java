package org.telegram.messenger.voip;

import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda55 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC$TL_groupCallParticipant f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda55(VoIPService voIPService, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant) {
        this.f$0 = voIPService;
        this.f$1 = tLRPC$TL_groupCallParticipant;
    }

    public final void run() {
        this.f$0.lambda$startGroupCall$25(this.f$1);
    }
}
