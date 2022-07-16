package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class DispatchQueuePoolBackground$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ArrayList f$0;

    public /* synthetic */ DispatchQueuePoolBackground$$ExternalSyntheticLambda0(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final void run() {
        DispatchQueuePoolBackground.lambda$finishCollectUpdateRunnables$3(this.f$0);
    }
}
