package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda66 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda66(MediaDataController mediaDataController, ArrayList arrayList, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = arrayList;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$clearBotKeyboard$165(this.f$1, this.f$2);
    }
}
