package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda77 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda77(MediaDataController mediaDataController, TLObject tLObject) {
        this.f$0 = mediaDataController;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$checkPremiumGiftStickers$60(this.f$1);
    }
}
