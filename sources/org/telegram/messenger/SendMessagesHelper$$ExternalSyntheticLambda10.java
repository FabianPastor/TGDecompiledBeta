package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ MessagesStorage.StringCallback f$0;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda10(MessagesStorage.StringCallback stringCallback) {
        this.f$0 = stringCallback;
    }

    public final void run() {
        this.f$0.run((String) null);
    }
}
