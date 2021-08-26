package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ MessagesStorage.IntCallback f$0;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda17(MessagesStorage.IntCallback intCallback) {
        this.f$0 = intCallback;
    }

    public final void run() {
        SendMessagesHelper.lambda$prepareImportHistory$67(this.f$0);
    }
}
