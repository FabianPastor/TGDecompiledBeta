package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda111 implements Runnable {
    public final /* synthetic */ MessagesStorage.IntCallback f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda111(MessagesStorage.IntCallback intCallback, int i) {
        this.f$0 = intCallback;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.run(this.f$1);
    }
}
