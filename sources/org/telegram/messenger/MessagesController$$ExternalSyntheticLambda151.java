package org.telegram.messenger;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda151 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ MessagesController.MessagesLoadedCallback f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda151(MessagesController messagesController, TLRPC$Chat tLRPC$Chat, long j, int i, MessagesController.MessagesLoadedCallback messagesLoadedCallback) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Chat;
        this.f$2 = j;
        this.f$3 = i;
        this.f$4 = messagesLoadedCallback;
    }

    public final void run() {
        this.f$0.lambda$ensureMessagesLoaded$352(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
