package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ MessagesStorage.LongCallback f$0;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda8(MessagesStorage.LongCallback longCallback) {
        this.f$0 = longCallback;
    }

    public final void run() {
        this.f$0.run(0);
    }
}
