package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda71 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda71(MediaDataController mediaDataController, TLObject tLObject, int i, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = i2;
    }

    public final void run() {
        this.f$0.lambda$loadStickers$51(this.f$1, this.f$2, this.f$3);
    }
}
