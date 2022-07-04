package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ MessagesStorage.LongCallback f$0;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda10(MessagesStorage.LongCallback longCallback) {
        this.f$0 = longCallback;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareImportHistory$67(this.f$0);
    }
}
