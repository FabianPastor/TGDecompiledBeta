package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class DispatchQueuePoolBackground$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ArrayList f$0;

    public /* synthetic */ DispatchQueuePoolBackground$$ExternalSyntheticLambda1(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final void run() {
        DispatchQueuePoolBackground.freeCollections.add(this.f$0);
    }
}
