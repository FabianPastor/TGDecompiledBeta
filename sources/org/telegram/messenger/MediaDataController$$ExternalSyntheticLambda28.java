package org.telegram.messenger;

import org.telegram.messenger.Utilities;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda28 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Utilities.Callback f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda28(MediaDataController mediaDataController, int i, Utilities.Callback callback) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = callback;
    }

    public final void run() {
        this.f$0.lambda$loadStickers$75(this.f$1, this.f$2);
    }
}
