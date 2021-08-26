package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda18 implements Runnable {
    public final /* synthetic */ MessagesStorage.IntCallback f$0;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda18(MessagesStorage.IntCallback intCallback) {
        this.f$0 = intCallback;
    }

    public final void run() {
        this.f$0.run(0);
    }
}
