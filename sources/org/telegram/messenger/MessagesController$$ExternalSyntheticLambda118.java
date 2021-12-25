package org.telegram.messenger;

import org.telegram.messenger.MessagesController;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda118 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ MessagesStorage f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ MessagesController.MessagesLoadedCallback f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda118(MessagesController messagesController, MessagesStorage messagesStorage, long j, long j2, int i, MessagesController.MessagesLoadedCallback messagesLoadedCallback) {
        this.f$0 = messagesController;
        this.f$1 = messagesStorage;
        this.f$2 = j;
        this.f$3 = j2;
        this.f$4 = i;
        this.f$5 = messagesLoadedCallback;
    }

    public final void run() {
        this.f$0.lambda$ensureMessagesLoaded$332(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
