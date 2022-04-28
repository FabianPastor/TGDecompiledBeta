package org.telegram.messenger.voip;

import org.telegram.tgnet.TLRPC$TL_updateGroupCall;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda57 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC$TL_updateGroupCall f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda57(VoIPService voIPService, TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall) {
        this.f$0 = voIPService;
        this.f$1 = tLRPC$TL_updateGroupCall;
    }

    public final void run() {
        this.f$0.lambda$startGroupCall$20(this.f$1);
    }
}
