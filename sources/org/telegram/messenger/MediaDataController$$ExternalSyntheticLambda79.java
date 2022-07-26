package org.telegram.messenger;

import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda79 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ Utilities.Callback f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda79(MediaDataController mediaDataController, TLObject tLObject, int i, Utilities.Callback callback, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = callback;
        this.f$4 = j;
    }

    public final void run() {
        this.f$0.lambda$loadStickers$78(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
