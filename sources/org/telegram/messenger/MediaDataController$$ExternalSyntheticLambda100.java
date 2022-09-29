package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_attachMenuBots;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda100 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$TL_attachMenuBots f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda100(MediaDataController mediaDataController, TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots, long j, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$TL_attachMenuBots;
        this.f$2 = j;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$putMenuBotsToCache$5(this.f$1, this.f$2, this.f$3);
    }
}
