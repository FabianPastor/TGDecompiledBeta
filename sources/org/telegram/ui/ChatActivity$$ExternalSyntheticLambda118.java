package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda118 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda118(ChatActivity chatActivity, TLObject tLObject) {
        this.f$0 = chatActivity;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$startEditingMessageObject$115(this.f$1);
    }
}
