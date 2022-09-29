package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda57 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ Integer f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ ArrayList[] f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda57(MediaDataController mediaDataController, Integer num, ArrayList arrayList, ArrayList[] arrayListArr, Runnable runnable) {
        this.f$0 = mediaDataController;
        this.f$1 = num;
        this.f$2 = arrayList;
        this.f$3 = arrayListArr;
        this.f$4 = runnable;
    }

    public final void run() {
        this.f$0.lambda$fillWithAnimatedEmoji$196(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
