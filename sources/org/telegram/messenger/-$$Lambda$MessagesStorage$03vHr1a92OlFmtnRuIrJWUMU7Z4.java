package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.IntCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$03vHr1a92OlFmtnRuIrJWUMU7Z4 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ IntCallback f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$03vHr1a92OlFmtnRuIrJWUMU7Z4(MessagesStorage messagesStorage, long j, IntCallback intCallback) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = intCallback;
    }

    public final void run() {
        this.f$0.lambda$getMessagesCount$97$MessagesStorage(this.f$1, this.f$2);
    }
}
