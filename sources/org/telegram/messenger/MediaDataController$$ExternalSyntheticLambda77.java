package org.telegram.messenger;

import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda77 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ Utilities.Callback f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda77(MediaDataController mediaDataController, Utilities.Callback callback, TLObject tLObject, int i, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = callback;
        this.f$2 = tLObject;
        this.f$3 = i;
        this.f$4 = j;
    }

    public final void run() {
        this.f$0.lambda$loadStickers$72(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
