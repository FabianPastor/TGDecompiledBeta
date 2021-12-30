package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda127 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda127(MessagesController messagesController, TLObject tLObject, int i) {
        this.f$0 = messagesController;
        this.f$1 = tLObject;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$loadGlobalNotificationsSettings$155(this.f$1, this.f$2);
    }
}
