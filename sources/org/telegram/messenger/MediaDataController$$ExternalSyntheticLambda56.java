package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda56 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda56(MediaDataController mediaDataController, ArrayList arrayList) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$broadcastPinnedMessage$111(this.f$1);
    }
}