package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda76 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda76(MediaDataController mediaDataController, TLObject tLObject) {
        this.f$0 = mediaDataController;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadHints$123(this.f$1);
    }
}
