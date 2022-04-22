package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ MessagesStorage.IntCallback f$0;
    public final /* synthetic */ int[] f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda4(MessagesStorage.IntCallback intCallback, int[] iArr) {
        this.f$0 = intCallback;
        this.f$1 = iArr;
    }

    public final void run() {
        this.f$0.run(this.f$1[0]);
    }
}
