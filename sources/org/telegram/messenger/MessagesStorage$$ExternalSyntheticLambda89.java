package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda89 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ MessagesStorage.IntCallback f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda89(MessagesStorage messagesStorage, long j, MessagesStorage.IntCallback intCallback) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = intCallback;
    }

    public final void run() {
        this.f$0.lambda$getDialogMaxMessageId$180(this.f$1, this.f$2);
    }
}
