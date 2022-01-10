package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda127 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda127(MessagesController messagesController, TLObject tLObject, long j, Runnable runnable) {
        this.f$0 = messagesController;
        this.f$1 = tLObject;
        this.f$2 = j;
        this.f$3 = runnable;
    }

    public final void run() {
        this.f$0.lambda$getGroupCall$34(this.f$1, this.f$2, this.f$3);
    }
}
