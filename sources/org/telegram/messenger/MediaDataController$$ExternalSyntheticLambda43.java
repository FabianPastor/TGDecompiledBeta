package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda43 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda43(MediaDataController mediaDataController, long j, ArrayList arrayList) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$loadReplyMessagesForMessages$134(this.f$1, this.f$2);
    }
}
