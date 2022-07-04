package org.telegram.messenger.voip;

import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda57 implements Runnable {
    public final /* synthetic */ VoIPService f$0;
    public final /* synthetic */ TLRPC$Updates f$1;

    public /* synthetic */ VoIPService$$ExternalSyntheticLambda57(VoIPService voIPService, TLRPC$Updates tLRPC$Updates) {
        this.f$0 = voIPService;
        this.f$1 = tLRPC$Updates;
    }

    public final void run() {
        this.f$0.lambda$startScreenCapture$30(this.f$1);
    }
}
