package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda119 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda119(MessagesController messagesController, TLObject tLObject) {
        this.f$0 = messagesController;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadSuggestedFilters$10(this.f$1);
    }
}