package org.telegram.messenger;

import org.telegram.messenger.Utilities;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ Utilities.Callback f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda26(MediaDataController mediaDataController, int i, boolean z, Utilities.Callback callback) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = callback;
    }

    public final void run() {
        this.f$0.lambda$loadStickers$69(this.f$1, this.f$2, this.f$3);
    }
}
