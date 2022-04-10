package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_attachMenuBots;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda90 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$TL_attachMenuBots f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda90(MediaDataController mediaDataController, TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$TL_attachMenuBots;
    }

    public final void run() {
        this.f$0.lambda$processLoadedMenuBots$4(this.f$1);
    }
}
