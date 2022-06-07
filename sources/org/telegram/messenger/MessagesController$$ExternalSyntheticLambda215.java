package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda215 implements Runnable {
    public final /* synthetic */ MessagesStorage.BooleanCallback f$0;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda215(MessagesStorage.BooleanCallback booleanCallback) {
        this.f$0 = booleanCallback;
    }

    public final void run() {
        MessagesController.lambda$convertToGigaGroup$219(this.f$0);
    }
}
