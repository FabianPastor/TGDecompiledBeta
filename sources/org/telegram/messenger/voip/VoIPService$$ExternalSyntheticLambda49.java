package org.telegram.messenger.voip;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda49 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda49(VoIPService voIPService, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = voIPService;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$startGroupCall$27(this.f$1);
    }
}
