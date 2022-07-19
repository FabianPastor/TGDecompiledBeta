package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda125 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ boolean[] f$1;
    public final /* synthetic */ ArrayList[] f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda125(MediaDataController mediaDataController, boolean[] zArr, ArrayList[] arrayListArr, Runnable runnable) {
        this.f$0 = mediaDataController;
        this.f$1 = zArr;
        this.f$2 = arrayListArr;
        this.f$3 = runnable;
    }

    public final void run() {
        this.f$0.lambda$fillWithAnimatedEmoji$183(this.f$1, this.f$2, this.f$3);
    }
}
