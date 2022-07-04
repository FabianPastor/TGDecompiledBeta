package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda60 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda60(MediaDataController mediaDataController, String str, TLObject tLObject) {
        this.f$0 = mediaDataController;
        this.f$1 = str;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$verifyAnimatedStickerMessageInternal$51(this.f$1, this.f$2);
    }
}
