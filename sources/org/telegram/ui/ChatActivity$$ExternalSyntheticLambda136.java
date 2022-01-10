package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda136 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda136(ChatActivity chatActivity, TLObject tLObject) {
        this.f$0 = chatActivity;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$startEditingMessageObject$140(this.f$1);
    }
}
